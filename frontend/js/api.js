import {session} from './state.js';

const API_BASE = `${location.protocol}//${location.hostname}:8080/api/v1`;

async function parse(response) {
  if (response.status === 204) return null;
  const body = await response.json().catch(() => ({message: 'Phản hồi từ máy chủ không hợp lệ.'}));
  if (!response.ok) {
    const error = new Error(body.message || 'Yêu cầu thất bại.');
    error.status = response.status;
    error.code = body.code;
    error.fields = body.fieldErrors || {};
    throw error;
  }
  return body.data;
}

async function refreshAccessToken() {
  const refreshToken = session.refresh();
  if (!refreshToken) throw new Error('Phiên đăng nhập đã hết hạn.');
  const response = await fetch(`${API_BASE}/auth/refresh`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({refreshToken})
  });
  const data = await parse(response);
  session.saveAuth(data);
  return data.accessToken;
}

export async function api(path, options = {}, retry = true) {
  const headers = {'Content-Type': 'application/json', ...(options.headers || {})};
  if (session.access()) headers.Authorization = `Bearer ${session.access()}`;
  const response = await fetch(`${API_BASE}${path}`, {...options, headers});
  if (response.status === 401 && retry && session.refresh()) {
    try {
      await refreshAccessToken();
      return api(path, options, false);
    } catch (error) {
      session.clear();
      location.hash = '/login';
      throw error;
    }
  }
  return parse(response);
}

export const request = (path, method = 'GET', body) => api(path, {
  method,
  ...(body === undefined ? {} : {body: JSON.stringify(body)})
});
