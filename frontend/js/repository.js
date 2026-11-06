import {api, request} from './api.js';
import {db, normalizeUser, session} from './state.js';

const lower = value => String(value || '').toLowerCase();
const id = value => value == null ? '' : String(value);
const numberId = value => Number(value);
const pageContent = value => value?.content || value || [];
const datePart = value => value ? String(value).slice(0, 10) : '';
const timePart = value => value ? new Date(value).toLocaleTimeString('vi-VN', {hour:'2-digit', minute:'2-digit'}) : '';

const member = x => ({...x, id:id(x.id), code:x.memberCode, name:x.fullName,
  birthday:x.dateOfBirth, status:lower(x.status)});
const employee = x => ({...x, id:id(x.id), code:x.employeeCode, name:x.fullName,
  role:x.employeeType, status:lower(x.status)});
const pack = x => ({...x, id:id(x.id), code:x.packageCode, type:(x.classTypes || []).join(', '),
  duration:Math.max(1, Math.round(x.durationDays / 30)), sessions:x.sessionCount,
  allowedClasses:x.classTypes || [], status:lower(x.status)});
const membership = x => ({...x, id:id(x.id), memberId:id(x.memberId), packageId:id(x.packageId),
  expiryDate:x.endDate, status:lower(x.status)});
const schedule = x => ({...x, id:id(x.id), classId:id(x.classId), code:x.classCode, name:x.className,
  type:x.classType, trainerId:id(x.trainerId), date:datePart(x.startsAt), startTime:timePart(x.startsAt),
  endTime:timePart(x.endsAt), status:lower(x.status === 'OPEN' ? 'active' : x.status)});
const registration = x => ({...x, id:id(x.id), classId:id(x.scheduleId), memberId:id(x.memberId),
  registeredAt:x.registeredAt || x.startsAt || '', status:lower(x.status === 'REGISTERED' ? 'active' : x.status)});
const attendance = x => ({...x, id:id(x.id), classId:id(x.scheduleId), memberId:id(x.memberId),
  date:datePart(x.startsAt || x.markedAt), status:lower(x.status)});
const payment = x => ({...x, id:id(x.id), code:x.transactionCode, memberId:id(x.memberId),
  date:datePart(x.createdAt), paymentTime:x.paidAt, status:lower(x.status)});

async function classTypes() { return request('/packages/class-types'); }
async function trainerRosterData() {
  const schedules = await request('/schedules');
  const groups = await Promise.all(schedules.map(async item => ({schedule:item, roster:await request(`/schedules/${item.id}/attendance-roster`)})));
  return groups;
}
async function nextCode(path, property, prefix, width) {
  const rows = pageContent(await request(path));
  const max = rows.reduce((result, row) => Math.max(result, Number(String(row[property] || '').match(/\d+/)?.[0] || 0)), 0);
  return `${prefix}-${String(max + 1).padStart(width, '0')}`;
}
async function packageBody(data, existing) {
  const types = await classTypes();
  const requested = String(data.allowedClasses || data.type || '').split(',').map(x => x.trim().toLowerCase()).filter(Boolean);
  const selected = types.filter(x => requested.some(value => lower(x.name) === value || lower(x.code) === value));
  return {
    packageCode: existing?.packageCode || data.code || await nextCode('/packages', 'packageCode', 'PK', 2),
    name:data.name, description:data.description || '', durationDays:Number(data.durationDays || data.duration) * (data.durationDays ? 1 : 30),
    sessionCount:Number(data.sessionCount || data.sessions), price:Number(data.price),
    classTypeIds:(selected.length ? selected : types.slice(0, 1)).map(x => x.id)
  };
}

export const repo = {
  auth: {
    login: async (email, password) => {
      const data = await request('/auth/login', 'POST', {email, password});
      session.saveAuth(data);
      const profile = await request('/profile');
      const user = normalizeUser({...data.user, ...profile});
      session.set(user);
      return user;
    },
    logout: async () => {
      try { await request('/auth/logout', 'POST', {refreshToken:session.refresh()}); } finally { session.clear(); }
    }
  },
  dashboard: {get: user => request(`/dashboard/${lower(user.role)}`)},
  reports: {overview: () => request('/reports/overview')},
  members: {
    list: async user => {
      if (user?.role === 'MEMBER') return [user];
      if (user?.role !== 'TRAINER') return pageContent(await request('/members?size=200')).map(member);
      const groups = await trainerRosterData();
      const unique = new Map();
      groups.flatMap(x => x.roster).forEach(x => unique.set(x.memberId, {
        id:id(x.memberId), code:x.memberCode, name:x.memberName, fullName:x.memberName, status:'active'
      }));
      return [...unique.values()];
    },
    create: async (_, data) => member(await request('/members', 'POST', {
      fullName:data.name, email:data.email, password:data.password, phone:data.phone,
      dateOfBirth:data.birthday || null, gender:data.gender, address:data.address, emergencyContact:data.emergencyContact || null
    })),
    update: async (_, memberId, data) => {
      let updated = await request(`/members/${numberId(memberId)}`, 'PATCH', {
        fullName:data.name, email:data.email, phone:data.phone, dateOfBirth:data.birthday || null, gender:data.gender,
        address:data.address, emergencyContact:data.emergencyContact || null
      });
      if (data.status && lower(updated.status) !== lower(data.status)) {
        updated = await request(`/members/${numberId(memberId)}/status`, 'PATCH', {status:String(data.status).toUpperCase()});
      }
      return member(updated);
    },
    remove: async (_, memberId) => member(await request(`/members/${numberId(memberId)}/status`, 'PATCH', {status:'INACTIVE'}))
  },
  employees: {
    list: async () => (await request('/employees')).map(employee),
    create: async (_, data) => employee(await request('/employees', 'POST', {
      employeeCode:data.code || await nextCode('/employees', 'employeeCode', data.role === 'TRAINER' ? 'HLV' : 'NV', 3),
      fullName:data.name, email:data.email, password:data.password, phone:data.phone,
      employeeType:data.role, specialization:data.specialization || '', biography:data.biography || ''
    })),
    update: async (_, employeeId, data) => {
      const existing = (await request('/employees')).find(item => item.id === numberId(employeeId));
      if (!existing) throw new Error('Không tìm thấy nhân sự cần cập nhật.');
      let updated = await request(`/employees/${numberId(employeeId)}`, 'PUT', {
        employeeCode:existing.employeeCode, fullName:data.name, email:data.email, phone:data.phone,
        employeeType:data.role, specialization:data.specialization ?? existing.specialization ?? '',
        biography:data.biography ?? existing.biography ?? ''
      });
      if (data.status && lower(updated.status) !== lower(data.status)) {
        updated = await request(`/employees/${numberId(employeeId)}/status`, 'PATCH', {status:String(data.status).toUpperCase()});
      }
      return employee(updated);
    },
    remove: async (_, employeeId) => employee(await request(`/employees/${numberId(employeeId)}/status`, 'PATCH', {status:'INACTIVE'}))
  },
  packages: {
    list: async () => (await request('/packages')).map(pack),
    create: async (_, data) => pack(await request('/packages', 'POST', await packageBody(data))),
    update: async (_, packageId, data) => {
      const existing = (await request('/packages')).find(x => x.id === numberId(packageId));
      let updated = await request(`/packages/${numberId(packageId)}`, 'PUT', await packageBody(data, existing));
      if (data.status && lower(updated.status) !== lower(data.status)) {
        updated = await request(`/packages/${numberId(packageId)}/status`, 'PATCH', {status:String(data.status).toUpperCase()});
      }
      return pack(updated);
    },
    remove: async (_, packageId) => pack(await request(`/packages/${numberId(packageId)}/status`, 'PATCH', {status:'INACTIVE'}))
  },
  classes: {
    list: async user => {
      if (['ADMIN','STAFF'].includes(user?.role)) {
        db.users = (await request('/employees')).filter(x => x.employeeType === 'TRAINER').map(employee);
      }
      return (await request('/schedules')).map(schedule);
    },
    create: async (_, data) => {
      const [types, employees] = await Promise.all([classTypes(), request('/employees')]);
      const type = types.find(x => lower(x.name) === lower(data.type)) || types[0];
      const classData = await request('/classes', 'POST', {
        classCode:data.code || await nextCode('/classes', 'classCode', 'LH', 3), name:data.name,
        classTypeId:type.id, location:data.location, defaultCapacity:Number(data.capacity)
      });
      const startsAt = new Date(`${data.date}T${data.startTime}`).toISOString();
      const endsAt = new Date(`${data.date}T${data.endTime}`).toISOString();
      const trainer = employees.find(x => id(x.id) === id(data.trainerId) && x.employeeType === 'TRAINER');
      return schedule(await request('/schedules', 'POST', {
        classId:classData.id, trainerId:Number(trainer?.id || data.trainerId), startsAt, endsAt, capacity:Number(data.capacity)
      }));
    },
    update: async (_, scheduleId, data) => {
      const [schedules, classes, types] = await Promise.all([request('/schedules'), request('/classes'), classTypes()]);
      const current = schedules.find(x => x.id === numberId(scheduleId));
      const currentClass = classes.find(x => x.id === current?.classId);
      if (!current || !currentClass) throw new Error('Không tìm thấy lớp học cần cập nhật.');
      const type = types.find(x => lower(x.name) === lower(data.type)) || types.find(x => x.id === currentClass.classTypeId) || types[0];
      await request(`/classes/${current.classId}`, 'PUT', {
        classCode:currentClass.classCode, name:data.name, classTypeId:type.id,
        location:data.location, defaultCapacity:Number(data.capacity)
      });
      const startsAt = new Date(`${data.date}T${data.startTime}`).toISOString();
      const endsAt = new Date(`${data.date}T${data.endTime}`).toISOString();
      let updated = await request(`/schedules/${numberId(scheduleId)}`, 'PUT', {
        classId:current.classId, trainerId:numberId(data.trainerId), startsAt, endsAt, capacity:Number(data.capacity)
      });
      const desiredStatus = ({active:'OPEN',inactive:'CLOSED',cancelled:'CANCELLED'})[lower(data.status)];
      if (desiredStatus && updated.status !== desiredStatus) {
        updated = await request(`/schedules/${numberId(scheduleId)}/status`, 'PATCH', {status:desiredStatus});
      }
      return schedule(updated);
    },
    remove: async (_, scheduleId) => schedule(await request(`/schedules/${numberId(scheduleId)}/status`, 'PATCH', {status:'CANCELLED'}))
  },
  memberships: {
    list: async user => {
      if (user.role === 'MEMBER') return (await request('/member-packages/me')).map(value => ({...membership(value), memberId:id(user.id)}));
      const members = pageContent(await request('/members?size=200'));
      const groups = await Promise.all(members.map(value => request(`/member-packages/member/${value.id}`)));
      return groups.flat().map(membership);
    },
    listForMember: async memberId => (await request(`/member-packages/member/${numberId(memberId)}`)).map(membership),
    create: async (user, data) => {
      const result = await request('/member-packages', 'POST', {
        memberId:user.role === 'MEMBER' ? null : numberId(data.memberId),
        packageId:numberId(data.packageId), paymentMethod:data.paymentMethod || 'CASH'
      });
      return membership(result.memberPackage);
    }
  },
  registrations: {
    list: async user => {
      if (user.role !== 'TRAINER') return (await request(user.role === 'MEMBER' ? '/class-registrations/me' : '/class-registrations')).map(registration);
      const groups = await trainerRosterData();
      return groups.flatMap(({schedule:item, roster}) => roster.map(x => ({
        id:id(x.registrationId), classId:id(item.id), memberId:id(x.memberId), registeredAt:item.startsAt, status:'active'
      })));
    },
    create: async (user, data) => registration(await request('/class-registrations', 'POST', {
      scheduleId:numberId(data.classId), memberId:user.role === 'MEMBER' ? null : (data.memberId ? numberId(data.memberId) : null),
      memberPackageId:numberId(data.membershipId)
    })),
    cancel: async (_, registrationId) => registration(await request(`/class-registrations/${numberId(registrationId)}/cancel`, 'PATCH', {reason:'Hủy từ giao diện quản lý'}))
  },
  attendance: {
    list: async user => {
      if (user.role === 'MEMBER') return (await request('/attendance/me')).map(attendance);
      if (user.role !== 'TRAINER') return [];
      const groups = await trainerRosterData();
      return groups.flatMap(({schedule:item, roster}) => roster.filter(x => x.attendanceStatus).map(x => ({
        id:`${item.id}-${x.memberId}`, classId:id(item.id), memberId:id(x.memberId), date:datePart(item.startsAt),
        status:lower(x.attendanceStatus)
      })));
    },
    roster: async scheduleId => request(`/schedules/${numberId(scheduleId)}/attendance-roster`),
    save: async (_, scheduleOrItems, explicitItems) => {
      const items = Array.isArray(scheduleOrItems) ? scheduleOrItems : explicitItems;
      const scheduleId = Array.isArray(scheduleOrItems) ? items[0]?.classId : scheduleOrItems;
      if (!scheduleId || !items?.length) throw new Error('Không có dữ liệu điểm danh để lưu.');
      return (await request(`/schedules/${numberId(scheduleId)}/attendance`, 'PUT', {
        items:items.map(x => ({memberId:numberId(x.memberId), status:String(x.status).toUpperCase()}))
      })).map(attendance);
    }
  },
  payments: {
    list: async user => pageContent(await request(user.role === 'MEMBER' ? '/payments/me' : '/payments?size=200')).map(payment),
    create: async (_, data) => {
      const result = await request('/member-packages', 'POST', {
        memberId:numberId(data.memberId), packageId:numberId(data.packageId),
        paymentMethod:({'Tiền mặt':'CASH','Chuyển khoản':'BANK_TRANSFER','Thẻ':'CARD'})[data.method] || 'CASH'
      });
      const confirmed = await request(`/payments/${result.payment.id}/status`, 'PATCH', {status:'PAID', reason:null});
      return payment(confirmed);
    },
    update: async (_, paymentId, data) => payment(await request(`/payments/${numberId(paymentId)}/status`, 'PATCH', {
      status:String(data.status).toUpperCase(), reason:data.reason || null
    }))
  },
  profile: {
    get: async () => normalizeUser(await request('/profile')),
    update: async (_, data) => normalizeUser(await request('/profile', 'PATCH', {
      fullName:data.name, phone:data.phone, dateOfBirth:data.birthday || null, gender:data.gender || null,
      address:data.address || null, emergencyContact:data.emergencyContact || null,
      specialization:data.specialization || null, biography:data.biography || null
    }))
  }
};
