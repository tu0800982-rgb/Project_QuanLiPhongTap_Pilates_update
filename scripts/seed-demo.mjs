const baseUrl = process.env.API_URL || 'http://127.0.0.1:8080/api/v1';
const demoPassword = process.env.DEMO_PASSWORD || '123456';

async function call(path, options = {}) {
  const response = await fetch(`${baseUrl}${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}), ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: options.body ? JSON.stringify(options.body) : undefined,
  });
  const payload = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(`${options.method || 'GET'} ${path}: ${response.status} ${payload.message || response.statusText}`);
  return payload.data;
}

let token = '';
const auth = await call('/auth/login', { method: 'POST', body: { email: 'admin@pilates.com', password: demoPassword } });
token = auth.accessToken;

const names = ['Nguyễn Minh Anh','Trần Gia Hân','Lê Hoàng Nam','Phạm Thu Trang','Võ Bảo Ngọc','Đặng Khánh Linh','Bùi Đức Anh','Hoàng Mai Phương','Đỗ Quỳnh Nhi','Ngô Thanh Tâm','Phan Nhật Minh','Lý Tuệ An','Hồ Gia Bảo'];
let membersPage = await call('/members?size=200');
let members = membersPage.content;
let createdMembers = 0;
for (let index = 0; index < names.length; index += 1) {
  const email = `demo.member${String(index + 2).padStart(2, '0')}@pilates.com`;
  if (members.some(item => item.email.toLowerCase() === email)) continue;
  const item = await call('/members', { method: 'POST', body: {
    fullName: names[index], email, password: demoPassword, phone: `091${String(index + 2).padStart(7, '0')}`,
    dateOfBirth: `199${index % 8}-${String(index % 9 + 1).padStart(2, '0')}-15`, gender: index % 3 ? 'FEMALE' : 'MALE',
    address: `${index + 12} Nguyễn Huệ, TP.HCM`, emergencyContact: `092${String(index + 2).padStart(7, '0')}`,
  }});
  members.push(item); createdMembers += 1;
}

const classTypes = await call('/packages/class-types');
const typeIds = classTypes.map(item => item.id);
const packageSeeds = [
  ['DEMO-PKG-02','Pilates Nền Tảng',30,12,1650000], ['DEMO-PKG-03','Pilates Toàn Diện',30,30,2450000],
  ['DEMO-PKG-04','Pilates Cá Nhân',30,8,3200000], ['DEMO-PKG-05','Pilates Chăm Sóc Sức Khỏe',90,36,4500000],
  ['DEMO-PKG-06','Pilates Cao Cấp',365,360,12000000],
];
let packages = await call('/packages');
let createdPackages = 0;
for (const [packageCode,name,durationDays,sessionCount,price] of packageSeeds) {
  if (packages.some(item => item.packageCode === packageCode)) continue;
  packages.push(await call('/packages', { method: 'POST', body: { packageCode, name, durationDays, sessionCount, price, classTypeIds:typeIds, description:'Gói dữ liệu trình diễn Pilates Studio.' } }));
  createdPackages += 1;
}

const classSeeds = ['Pilates Core & Kiểm Soát','Pilates Dẻo Dai Nhẹ Nhàng','Pilates Reformer Săn Chắc','Pilates Mat Phục Hồi','Pilates Cá Nhân 1:1','Pilates Reformer Năng Lượng','Pilates Chuyển Động Tỉnh Thức','Pilates Cân Bằng & Sức Mạnh','Pilates Buổi Tối'];
let classes = await call('/classes');
let createdClasses = 0;
for (let index = 0; index < classSeeds.length; index += 1) {
  const classCode = `DEMO-CLS-${String(index + 2).padStart(2, '0')}`;
  if (classes.some(item => item.classCode === classCode)) continue;
  classes.push(await call('/classes', { method: 'POST', body: { classCode, name:classSeeds[index], classTypeId:typeIds[index % typeIds.length], location:index % 2 ? 'Studio B' : 'Studio A', defaultCapacity:index === 4 ? 1 : 10 } }));
  createdClasses += 1;
}

const employees = await call('/employees');
const trainer = employees.find(item => item.employeeType === 'TRAINER' && item.status === 'ACTIVE');
let schedules = await call('/schedules');
let createdSchedules = 0;
for (let index = 0; index < classes.filter(item => item.status === 'ACTIVE').length; index += 1) {
  const studioClass = classes.filter(item => item.status === 'ACTIVE')[index];
  if (schedules.some(item => item.classId === studioClass.id && item.status === 'OPEN')) continue;
  const startsAt = new Date(Date.now() + (index + 2) * 86400000 + (index % 2 ? 10 : 2) * 3600000);
  const endsAt = new Date(startsAt.getTime() + 3600000);
  schedules.push(await call('/schedules', { method: 'POST', body: { classId:studioClass.id, trainerId:trainer.id, startsAt:startsAt.toISOString(), endsAt:endsAt.toISOString(), capacity:studioClass.defaultCapacity } }));
  createdSchedules += 1;
}

membersPage = await call('/members?size=200');
members = membersPage.content.filter(item => item.status === 'ACTIVE');
packages = (await call('/packages')).filter(item => item.status === 'ACTIVE');
let createdMemberships = 0;
let confirmedPayments = 0;
for (let index = 0; index < members.length; index += 1) {
  const member = members[index];
  const existing = await call(`/member-packages/member/${member.id}`);
  if (existing.length) continue;
  const result = await call('/member-packages', { method: 'POST', body: { memberId:member.id, packageId:packages[index % packages.length].id, paymentMethod:index % 2 ? 'CARD' : 'BANK_TRANSFER' } });
  createdMemberships += 1;
  if (index % 5 !== 4) {
    await call(`/payments/${result.payment.id}/status`, { method: 'PATCH', body: { status:'PAID', reason:null } });
    confirmedPayments += 1;
  }
}

schedules = (await call('/schedules')).filter(item => item.status === 'OPEN');
let registrations = await call('/class-registrations');
const activeMemberships = new Map();
for (const member of members) {
  const owned = await call(`/member-packages/member/${member.id}`);
  const usable = owned.find(item => item.status === 'ACTIVE' && item.remainingSessions > 0);
  if (usable) activeMemberships.set(member.id, usable);
}
let createdRegistrations = 0;
for (let scheduleIndex = 0; scheduleIndex < schedules.length; scheduleIndex += 1) {
  const item = schedules[scheduleIndex];
  const target = Math.min(item.capacity, 4);
  let current = registrations.filter(value => value.scheduleId === item.id && value.status === 'REGISTERED');
  for (let offset = 0; current.length < target && offset < members.length; offset += 1) {
    const member = members[(scheduleIndex * 3 + offset) % members.length];
    const owned = activeMemberships.get(member.id);
    if (!owned || current.some(value => value.memberId === member.id)) continue;
    try {
      const registration = await call('/class-registrations', { method:'POST', body:{ scheduleId:item.id, memberId:member.id, memberPackageId:owned.id } });
      registrations.push(registration); current.push(registration); createdRegistrations += 1;
      owned.remainingSessions -= 1;
    } catch (error) {
      if (!/trùng|duplicate|sức chứa|lịch|không áp dụng/i.test(error.message)) throw error;
    }
  }
}

let seededAttendance = 0;
for (const schedule of schedules) {
  const roster = await call(`/schedules/${schedule.id}/attendance-roster`);
  if (!roster.length) continue;
  await call(`/schedules/${schedule.id}/attendance`, { method:'PUT', body:{items:roster.map((item,index) => ({memberId:item.memberId, status:['PRESENT','ABSENT','EXCUSED'][index % 3]}))} });
  seededAttendance += roster.length;
}

const finalMembers = await call('/members?size=200');
const finalPayments = await call('/payments?size=200');
console.log(JSON.stringify({
  created:{members:createdMembers,packages:createdPackages,classes:createdClasses,schedules:createdSchedules,memberships:createdMemberships,confirmedPayments,registrations:createdRegistrations,attendance:seededAttendance},
  totals:{members:finalMembers.totalElements,packages:(await call('/packages')).length,classes:(await call('/classes')).length,schedules:(await call('/schedules')).length,registrations:(await call('/class-registrations')).length,payments:finalPayments.totalElements}
}, null, 2));
