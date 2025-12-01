import React from 'react';
import { Link } from 'react-router-dom';
import { getToken } from './api';

export default function App(){
  const token = getToken();
  return (
    <div style={{padding:24}}>
      <h1>Zidio — Frontend</h1>
      {token ? (
        <>
          <p>You're logged in (token stored). Try calling backend APIs.</p>
          <pre style={{wordBreak:'break-all', background:'#f3f4f6', padding:10}}>{token}</pre>
        </>
      ) : (
        <>
          <p>You are not signed in.</p>
          <Link to="/login">Go to login</Link>
        </>
      )}
    </div>
  )
}
