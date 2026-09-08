export default function LoginPage() {
  // TODO: wire up form state and submit handler (call authClient, on success
  // call authStore.login(tokens, user), navigate to /dashboard)
  return (
    <div>
      <h1>Login</h1>
      <form>
        <div>
          <label htmlFor="username">Username</label>
          <input id="username" name="username" type="text" />
        </div>
        <div>
          <label htmlFor="password">Password</label>
          <input id="password" name="password" type="password" />
        </div>
        <button type="submit">Log in</button>
      </form>
    </div>
  );
}
