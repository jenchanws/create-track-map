const signalIcon = (color) =>
  L.divIcon({
    html: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="16"
      height="16"
      viewBox="0 0 64 64"
    >
      <path class="frame" d="M44 0c11.046 0 20 8.954 20 20 0 10.37-7.893 18.897-17.999 19.901L46 60h18v4H24v-4h18V39.901C31.893 38.898 24 30.371 24 20 24 8.954 32.954 0 44 0Zm0 4c-8.837 0-16 7.163-16 16s7.163 16 16 16 16-7.163 16-16S52.837 4 44 4Z"/>
      <path class="light" d="M44 4c-8.837 0-16 7.163-16 16s7.163 16 16 16 16-7.163 16-16S52.837 4 44 4Z"/>
    </svg>`,
    className: `signal-icon ${color}`,
    iconSize: [16, 16],
    iconAnchor: [2, 16],
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
