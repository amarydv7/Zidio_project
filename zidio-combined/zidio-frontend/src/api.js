// src/api.js
export const API_BASE = "http://localhost:8080";

export async function login(email, password) {
  const res = await fetch(`${API_BASE}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  if (!res.ok) {
    let msg = "Login failed";
    try { const j = await res.json(); msg = j.message || j.error || msg; } catch(e){}
    throw new Error(msg);
  }
  return res.json();
}

export function saveToken(token){ localStorage.setItem('zidio_token', token); }
export function getToken(){ return localStorage.getItem('zidio_token'); }
export function authHeader(){ const t = getToken(); return t ? { Authorization: `Bearer ${t}` } : {}; }
