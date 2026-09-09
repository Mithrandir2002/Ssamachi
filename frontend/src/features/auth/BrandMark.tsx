/** Compass rose with a seismic trace running through it. */
export default function BrandMark({ className = "" }: { className?: string }) {
  return (
    <svg viewBox="0 0 48 48" className={className} fill="none" aria-hidden>
      <circle cx="24" cy="24" r="21" stroke="currentColor" strokeWidth="1.6" opacity="0.55" />
      <circle cx="24" cy="24" r="15" stroke="currentColor" strokeWidth="1" opacity="0.3" />

      {/* cardinal ticks */}
      <g stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" opacity="0.65">
        <path d="M24 3v5" />
        <path d="M24 40v5" />
        <path d="M3 24h5" />
        <path d="M40 24h5" />
      </g>

      {/* needle */}
      <path d="M24 9 L29 24 L24 39 L19 24 Z" fill="currentColor" opacity="0.18" />
      <path d="M24 9 L29 24 L24 39" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" />

      {/* seismic trace */}
      <path
        d="M8 24h6l3-7 4 15 3-11 3 6 2-3h9"
        stroke="var(--color-rust)"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}
