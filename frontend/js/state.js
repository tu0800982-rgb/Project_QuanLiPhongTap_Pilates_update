const ACCESS_KEY = 'pilates-access-token';
const REFRESH_KEY = 'pilates-refresh-token';
const USER_KEY = 'pilates-user';

export const session = {
  access: () => localStorage.getItem(ACCESS_KEY),
  refresh: () => localStorage.getItem(REFRESH_KEY),
  get: () => {
    try { return JSON.parse(localStorage.getItem(USER_KEY) || 'null'); }
    catch { return null; }
  },
  saveAuth: data => {
    localStorage.setItem(ACCESS_KEY, data.accessToken);
    localStorage.setItem(REFRESH_KEY, data.refreshToken);
    session.set(normalizeUser(data.user));
  },
  set: user => localStorage.setItem(USER_KEY, JSON.stringify(normalizeUser(user))),
  clear: () => {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
  }
};

export const normalizeUser = user => user ? {
  ...user,
  id: user.id ?? user.userId,
  name: user.name ?? user.fullName,
  role: String(user.role || '').toUpperCase(),
  status: String(user.status || 'ACTIVE').toLowerCase()
} : null;

export const money = value => new Intl.NumberFormat('vi-VN', {
  style: 'currency', currency: 'VND', maximumFractionDigits: 0
}).format(Number(value || 0));

// Cache UI-only for select options; business data always comes from the API.
export const db = {users: []};
