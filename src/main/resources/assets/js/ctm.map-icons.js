const autoSignalIcon = (color, leftSide) =>
  L.divIcon({
    html: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="10"
      height="16"
      viewBox="0 0 40 64"
    >
      <path class="frame" d="M20 0c11.046 0 20 8.954 20 20 0 10.37-7.893 18.897-17.999 19.901L22 60h18v4H0v-4h18V39.901C7.893 38.898 0 30.371 0 20 0 8.954 8.954 0 20 0Zm0 4C11.163 4 4 11.163 4 20s7.163 16 16 16 16-7.163 16-16S28.837 4 20 4Z"/>
      <path class="light" d="M20 4C11.163 4 4 11.163 4 20s7.163 16 16 16 16-7.163 16-16S28.837 4 20 4Z"/>
    </svg>`,
    className: `signal-icon ${color}`,
    iconSize: [10, 16],
    iconAnchor: [leftSide ? 14 : -4, 16],
  })

const chainSignalIcon = (color, leftSide) =>
  L.divIcon({
    html: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="10"
      height="16"
      viewBox="0 0 40 64"
    >
      <path class="frame" d="m25.168 3.074.117.21.108.21L38.99 31.368a6 6 0 0 1-5.168 8.627l-.225.004H22v20h18v4H0v-4h18V40H6.402a6 6 0 0 1-5.49-8.422l.097-.209L14.607 3.493a6 6 0 0 1 2.508-2.63L17.37.73l.21-.097a6 6 0 0 1 7.589 2.44Zm-3.37 2.173a2 2 0 0 0-3.596 0L4.604 33.123A2 2 0 0 0 6.402 36h27.196a2 2 0 0 0 1.798-2.877Z"/>
      <path class="light" d="m21.798 5.247 13.598 27.876A2 2 0 0 1 33.598 36H6.402a2 2 0 0 1-1.798-2.877L18.202 5.247a2 2 0 0 1 3.596 0Z"/>
    </svg>`,
    className: `signal-icon ${color}`,
    iconSize: [10, 16],
    iconAnchor: [leftSide ? 14 : -4, 16],
  })

const stationIcon = L.divIcon({
  html: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="18"
      height="18"
      viewBox="0 0 64 64"
    >
      <path class="outline" d="M56 0a8 8 0 0 1 8 8v48a8 8 0 0 1-8 8H8a8 8 0 0 1-8-8V8a8 8 0 0 1 8-8h48Z"/>
      <path class="fill" d="M56 4H8a4 4 0 0 0-3.995 3.8L4 8v48a4 4 0 0 0 3.8 3.995L8 60h48a4 4 0 0 0 3.995-3.8L60 56V8a4 4 0 0 0-3.8-3.995L56 4Z"/>
      <path class="outline" d="m33.287 16.466.127.116 14 14 .117.128a2 2 0 0 1 0 2.574l-.117.127-.127.117a2 2 0 0 1-2.574 0l-.127-.117-10.587-10.586L34 49.997l-.005.149a2 2 0 0 1-3.99 0l-.005-.15V22.826L19.413 33.411l-.127.117a2 2 0 0 1-2.818-2.818l.117-.128 14-14 .127-.116a2 2 0 0 1 2.574 0ZM50.36 10l.149.005a2 2 0 0 1 .003 3.99l-.149.005-36 .033-.15-.005a2 2 0 0 1-.003-3.99l.15-.005 36-.033Z"/>
    </svg>`,
  className: "station-icon",
  iconSize: [18, 18],
  iconAnchor: [9, 9],
})

const portalIcon = L.divIcon({
  html: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 64 64"
    >
      <path class="outline" d="M56 0a8 8 0 0 1 8 8v48a8 8 0 0 1-8 8H8a8 8 0 0 1-8-8V8a8 8 0 0 1 8-8h48Z"/>
      <path class="fill" d="M56 4H8a4 4 0 0 0-3.995 3.8L4 8v48a4 4 0 0 0 3.8 3.995L8 60h48a4 4 0 0 0 3.995-3.8L60 56V8a4 4 0 0 0-3.8-3.995L56 4Z"/>
      <path class="outline" d="m29.287 18.469.127.117 12 12a2 2 0 0 1 .17 2.635l-.114.136-12 13a2 2 0 0 1-3.051-2.582l.111-.132L35.431 34H10a2 2 0 0 1-.15-3.995L10 30h25.171l-8.585-8.586a2 2 0 0 1-.117-2.701l.117-.127a2 2 0 0 1 2.701-.117Z"/>
      <path class="outline" d="M49 9a6 6 0 0 1 5.996 5.775L55 15v34a6 6 0 0 1-5.775 5.996L49 55h-8a6 6 0 0 1-5.996-5.775L35 49v-3a2 2 0 0 1 3.995-.15L39 46v3a2 2 0 0 0 1.85 1.995L41 51h8a2 2 0 0 0 1.995-1.85L51 49V15a2 2 0 0 0-1.85-1.995L49 13h-8a2 2 0 0 0-1.995 1.85L39 15v4a2 2 0 0 1-3.995.15L35 19v-4a6 6 0 0 1 5.775-5.996L41 9h8Z"/>
    </svg>`,
  className: "portal-icon",
  iconSize: [24, 24],
})

const headIcon = L.divIcon({
  html: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="10"
      height="12"
      viewBox="0 0 10 12"
    >
      <path d="m10 6-6 6H0V0h4z"/>
    </svg>`,
  className: "head-icon",
  iconSize: [10, 12],
  iconAnchor: [-2, 7],
})
