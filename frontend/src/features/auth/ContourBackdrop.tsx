/** Subtle topographic contour lines, the way an old survey map looks. */
export default function ContourBackdrop() {
  return (
    <div aria-hidden className="pointer-events-none fixed inset-0 overflow-hidden">
      <svg
        className="h-full w-full"
        viewBox="0 0 1200 800"
        preserveAspectRatio="xMidYMid slice"
      >
        <defs>
          {/* One irregular ring; the clusters below just rescale it. */}
          <path
            id="contour-ring"
            d="M0,-100 C55,-104 104,-58 100,-4 C96,52 54,102 -2,100 C-58,98 -104,54 -100,-2 C-96,-58 -56,-96 0,-100 Z"
          />
          <g id="contour-cluster">
            <use href="#contour-ring" transform="scale(0.26) rotate(6)" />
            <use href="#contour-ring" transform="scale(0.44) rotate(-5)" />
            <use href="#contour-ring" transform="scale(0.62) rotate(9)" />
            <use href="#contour-ring" transform="scale(0.81) rotate(-8)" />
            <use href="#contour-ring" transform="scale(1) rotate(4)" />
            <use href="#contour-ring" transform="scale(1.21) rotate(-11)" />
            <use href="#contour-ring" transform="scale(1.44) rotate(7)" />
            <use href="#contour-ring" transform="scale(1.69) rotate(-3)" />
          </g>
        </defs>

        <g fill="none" stroke="var(--color-sepia-line)" strokeWidth="1.1">
          <g opacity="0.34">
            <use href="#contour-cluster" transform="translate(150 165) scale(1.32)" />
          </g>
          <g opacity="0.26">
            <use href="#contour-cluster" transform="translate(1035 610) scale(1.6)" />
          </g>
          <g opacity="0.2">
            <use href="#contour-cluster" transform="translate(960 120) scale(0.9)" />
          </g>
          <g opacity="0.18">
            <use href="#contour-cluster" transform="translate(255 690) scale(1.05)" />
          </g>

          {/* Long ridge lines drifting across the sheet. */}
          <g opacity="0.22" strokeWidth="1">
            <path d="M-40,300 C220,250 340,392 600,352 C860,312 980,168 1240,214" />
            <path d="M-40,340 C220,292 348,432 600,392 C852,352 986,214 1240,258" />
            <path d="M-40,382 C220,336 356,472 600,432 C844,392 992,258 1240,300" />
            <path d="M-40,506 C260,470 420,586 700,552 C900,528 1040,470 1240,486" />
            <path d="M-40,548 C260,514 428,626 700,592 C904,568 1044,512 1240,528" />
          </g>
        </g>
      </svg>
    </div>
  );
}
