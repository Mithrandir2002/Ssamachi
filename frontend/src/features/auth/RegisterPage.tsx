export default function RegisterPage() {
  // TODO: wire up form state and submit handler (call authClient, on success
  // navigate to /login)
  return (
    <div>
      <h1>Register</h1>
      <form>
        <div>
          <label htmlFor="username">Username</label>
          <input id="username" name="username" type="text" />
        </div>
        <div>
          <label htmlFor="email">Email</label>
          <input id="email" name="email" type="email" />
        </div>
        <div>
          <label htmlFor="password">Password</label>
          <input id="password" name="password" type="password" />
        </div>
        <button type="submit">Register</button>
      </form>
    </div>
  );
}
