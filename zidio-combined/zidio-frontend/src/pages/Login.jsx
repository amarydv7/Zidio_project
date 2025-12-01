import React, { useState } from 'react';
import { login, saveToken } from '../api';
import '../styles.css';

export default function Login(){
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e){
    e.preventDefault();
    setError('');
    if(!email || !password){ setError('Enter email and password'); return; }
    setLoading(true);
    try {
      const resp = await login(email, password);
      const token = resp.token || resp.jwt || resp.accessToken;
      if(!token) throw new Error('No token returned by backend');
      saveToken(token);
      window.location.href = '/';
    } catch(err){
      setError(err.message || 'Login failed');
    } finally { setLoading(false); }
  }

  return (
    <div className="login-root">
      <form className="login-card" onSubmit={handleSubmit}>
        <h2>Sign in</h2>
        {error && <div className="login-error">{error}</div>}
        <label>
          Email
          <input type="email" value={email} onChange={e=>setEmail(e.target.value)} placeholder="you@example.com"/>
        </label>
        <label>
          Password
          <input type="password" value={password} onChange={e=>setPassword(e.target.value)} placeholder="password"/>
        </label>
        <button type="submit" disabled={loading}>{loading ? 'Signing in...' : 'Sign in'}</button>
      </form>
    </div>
  );
}
