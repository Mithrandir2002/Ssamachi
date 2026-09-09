import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import authClient from "../../api/authClient";
import BrandMark from "./BrandMark";
import ContourBackdrop from "./ContourBackdrop";

type RegisterForm = {
  fullName: string;
  username: string;
  email: string;
  password: string;
};

type RegisterResponse = {
  email: string;
  message: string;
  expiresInSeconds: number;
};

const EMPTY_FORM: RegisterForm = { fullName: "", username: "", email: "", password: "" };

const inputClass =
  "w-full rounded-full border border-sepia-line/50 bg-parchment/70 px-5 py-3 text-umber " +
  "placeholder:text-umber-soft/50 shadow-inner outline-none transition " +
  "focus:border-rust focus:bg-parchment focus:ring-4 focus:ring-rust/15";

function errorMessage(err: unknown): string {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data as { message?: string; error?: string } | undefined;
    if (data?.message) return data.message;
    if (err.response?.status === 409) return "Username hoặc email đã tồn tại.";
    if (err.response?.status === 400) return "Dữ liệu chưa hợp lệ, kiểm tra lại các trường.";
    if (data?.error) return data.error;
    if (!err.response) return "Không kết nối được tới auth-service. Service đã chạy chưa?";
    return `Lỗi ${err.response.status} từ máy chủ.`;
  }
  return "Có lỗi không xác định xảy ra.";
}

export default function RegisterPage() {
  const [form, setForm] = useState<RegisterForm>(EMPTY_FORM);
  const [showPassword, setShowPassword] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [pending, setPending] = useState<RegisterResponse | null>(null);
  const [code, setCode] = useState("");
  const [secondsLeft, setSecondsLeft] = useState(0);
  const [verifying, setVerifying] = useState(false);
  const [verified, setVerified] = useState(false);

  useEffect(() => {
    if (!pending) return;
    setSecondsLeft(pending.expiresInSeconds);
    const timer = setInterval(() => {
      setSecondsLeft((left) => (left > 0 ? left - 1 : 0));
    }, 1000);
    return () => clearInterval(timer);
  }, [pending]);

  const countdown = useMemo(() => {
    const minutes = Math.floor(secondsLeft / 60);
    const seconds = secondsLeft % 60;
    return `${minutes}:${String(seconds).padStart(2, "0")}`;
  }, [secondsLeft]);

  const update = (field: keyof RegisterForm) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setForm((current) => ({ ...current, [field]: event.target.value }));
  };

  async function handleRegister(event: React.FormEvent) {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const { data } = await authClient.post<RegisterResponse>("/api/auth/register", form);
      setPending(data);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  async function handleVerify(event: React.FormEvent) {
    event.preventDefault();
    if (!pending) return;
    setError(null);
    setVerifying(true);
    try {
      await authClient.post("/api/auth/register/verify", { email: pending.email, code });
      setVerified(true);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setVerifying(false);
    }
  }

  return (
    <div className="relative min-h-screen overflow-hidden bg-parchment">
      <ContourBackdrop />

      <div className="relative mx-auto flex min-h-screen w-full max-w-lg flex-col justify-center px-5 py-12">
        <header className="mb-8 flex items-center gap-3">
          <BrandMark className="h-11 w-11 text-umber" />
          <div>
            <p className="text-lg font-semibold tracking-tight text-umber">Seismic Atlas</p>
            <p className="text-xs uppercase tracking-[0.22em] text-umber-soft/80">
              Earthquake survey network
            </p>
          </div>
        </header>

        <main className="rounded-3xl border border-sepia-line/40 bg-parchment-deep/60 p-7 shadow-warm backdrop-blur-sm sm:p-9">
          {verified ? (
            <VerifiedPanel email={pending?.email ?? ""} />
          ) : pending ? (
            <form onSubmit={handleVerify} className="space-y-5">
              <div>
                <h1 className="text-2xl font-semibold tracking-tight text-umber">Nhập mã xác thực</h1>
                <p className="mt-2 text-sm text-umber-soft">
                  Đã gửi mã 6 số tới{" "}
                  <span className="font-medium text-terracotta">{pending.email}</span>.
                </p>
              </div>

              <input
                value={code}
                onChange={(event) => setCode(event.target.value.replace(/\D/g, "").slice(0, 6))}
                inputMode="numeric"
                autoComplete="one-time-code"
                placeholder="000000"
                className={`${inputClass} text-center text-2xl font-semibold tracking-[0.55em]`}
              />

              <div className="flex items-center justify-between text-sm text-umber-soft">
                <span>
                  Mã hết hạn sau <span className="font-semibold text-terracotta">{countdown}</span>
                </span>
                <button
                  type="button"
                  onClick={() => {
                    setPending(null);
                    setCode("");
                    setError(null);
                  }}
                  className="rounded-full px-3 py-1 underline decoration-sepia-line underline-offset-4 transition hover:text-rust"
                >
                  Đổi thông tin
                </button>
              </div>

              {error && <ErrorNote message={error} />}

              <SubmitButton busy={verifying} label="Xác thực" busyLabel="Đang kiểm tra…" />
            </form>
          ) : (
            <form onSubmit={handleRegister} className="space-y-5">
              <div>
                <h1 className="text-2xl font-semibold tracking-tight text-umber">Tạo tài khoản</h1>
                <p className="mt-2 text-sm text-umber-soft">
                  Theo dõi chấn động toàn cầu theo thời gian thực.
                </p>
              </div>

              <Field label="Họ và tên" htmlFor="fullName">
                <input
                  id="fullName"
                  value={form.fullName}
                  onChange={update("fullName")}
                  autoComplete="name"
                  placeholder="Nguyễn Văn A"
                  className={inputClass}
                />
              </Field>

              <Field label="Tên đăng nhập" htmlFor="username">
                <input
                  id="username"
                  value={form.username}
                  onChange={update("username")}
                  autoComplete="username"
                  required
                  minLength={3}
                  placeholder="tối thiểu 3 ký tự"
                  className={inputClass}
                />
              </Field>

              <Field label="Email" htmlFor="email">
                <input
                  id="email"
                  type="email"
                  value={form.email}
                  onChange={update("email")}
                  autoComplete="email"
                  required
                  placeholder="ban@example.com"
                  className={inputClass}
                />
              </Field>

              <Field label="Mật khẩu" htmlFor="password">
                <div className="relative">
                  <input
                    id="password"
                    type={showPassword ? "text" : "password"}
                    value={form.password}
                    onChange={update("password")}
                    autoComplete="new-password"
                    required
                    minLength={8}
                    placeholder="tối thiểu 8 ký tự"
                    className={`${inputClass} pr-14`}
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword((visible) => !visible)}
                    aria-label={showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu"}
                    className="absolute inset-y-0 right-2 my-auto flex h-9 w-9 items-center justify-center rounded-full text-umber-soft transition hover:bg-sepia-line/20 hover:text-umber"
                  >
                    <EyeIcon off={showPassword} />
                  </button>
                </div>
              </Field>

              {error && <ErrorNote message={error} />}

              <SubmitButton busy={submitting} label="Tạo tài khoản" busyLabel="Đang gửi mã…" />

              <p className="text-center text-sm text-umber-soft">
                Đã có tài khoản?{" "}
                <Link to="/login" className="font-medium text-rust underline underline-offset-4">
                  Đăng nhập
                </Link>
              </p>
            </form>
          )}
        </main>

        <p className="mt-6 text-center text-xs tracking-wide text-umber-soft/70">
          Dữ liệu chấn động cung cấp bởi USGS
        </p>
      </div>
    </div>
  );
}

function Field({
  label,
  htmlFor,
  children,
}: {
  label: string;
  htmlFor: string;
  children: React.ReactNode;
}) {
  return (
    <div className="space-y-1.5">
      <label
        htmlFor={htmlFor}
        className="ml-4 block text-xs font-medium uppercase tracking-[0.14em] text-umber-soft"
      >
        {label}
      </label>
      {children}
    </div>
  );
}

function ErrorNote({ message }: { message: string }) {
  return (
    <p
      role="alert"
      className="rounded-2xl border border-terracotta/40 bg-terracotta-soft/25 px-4 py-3 text-sm text-terracotta"
    >
      {message}
    </p>
  );
}

/** Rust CTA with a seismograph trace travelling underneath the label. */
function SubmitButton({
  busy,
  label,
  busyLabel,
}: {
  busy: boolean;
  label: string;
  busyLabel: string;
}) {
  // One 120-unit period, repeated — matches the translateX(-120px) keyframe.
  const wave = Array.from({ length: 5 }, (_, index) => index * 120)
    .map(
      (x) =>
        `${x},24 ${x + 28},24 ${x + 34},9 ${x + 40},39 ${x + 46},15 ${x + 52},33 ${x + 58},24 ${x + 120},24`,
    )
    .join(" ");

  return (
    <button
      type="submit"
      disabled={busy}
      className="group relative flex h-13 w-full items-center justify-center overflow-hidden rounded-full bg-rust py-3.5 font-semibold text-parchment shadow-warm-sm transition hover:bg-rust-deep focus:outline-none focus:ring-4 focus:ring-rust/25 disabled:opacity-70"
    >
      <svg
        aria-hidden
        viewBox="0 0 360 48"
        preserveAspectRatio="none"
        className="pointer-events-none absolute inset-0 h-full w-full opacity-0 transition-opacity duration-300 group-hover:opacity-45 group-focus:opacity-45"
        style={busy ? { opacity: 0.6 } : undefined}
      >
        <polyline
          points={wave}
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
          className={busy ? "animate-seismic" : "group-hover:animate-seismic"}
        />
      </svg>
      <span className="relative z-10">{busy ? busyLabel : label}</span>
    </button>
  );
}

function VerifiedPanel({ email }: { email: string }) {
  return (
    <div className="space-y-4 text-center">
      <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-full bg-rust/12 text-rust">
        <svg viewBox="0 0 24 24" className="h-7 w-7" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M4 12.5 9 17.5 20 6.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      </div>
      <h1 className="text-2xl font-semibold tracking-tight text-umber">Đăng ký thành công</h1>
      <p className="text-sm text-umber-soft">
        Tài khoản <span className="font-medium text-terracotta">{email}</span> đã được kích hoạt.
      </p>
      <Link
        to="/login"
        className="inline-flex items-center justify-center rounded-full bg-rust px-6 py-3 font-semibold text-parchment shadow-warm-sm transition hover:bg-rust-deep"
      >
        Đăng nhập ngay
      </Link>
    </div>
  );
}

function EyeIcon({ off }: { off: boolean }) {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8">
      <path
        d="M2.5 12S6 5.5 12 5.5 21.5 12 21.5 12 18 18.5 12 18.5 2.5 12 2.5 12Z"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle cx="12" cy="12" r="3.2" />
      {off && <path d="M4 20 20 4" strokeLinecap="round" />}
    </svg>
  );
}
