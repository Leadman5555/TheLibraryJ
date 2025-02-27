export function parseDateString(date: string): string {
  const splitIndex = date.indexOf('T');
  const calendarParts = date.substring(0, splitIndex).split('-');
  return `${calendarParts[0]}-${calendarParts[1].padStart(2, '0')}-${calendarParts[2].padStart(2, '0')} | ${date.substring(splitIndex + 1, splitIndex + 6)}`;
}

export function hoursFromNow(dateString: string): number {
  return Math.floor((new Date(dateString).getTime() - new Date().getTime()) / 3600000);
}

export function hoursFromNowAsHourString(dateString: string): string {
  const hours = hoursFromNow(dateString);
  return hours === 1 ? `one hour` : `${hours} hours`;
}

// noinspection JSUnusedGlobalSymbols
/**
 * @deprecated Use the parseDateString instead
 */
export function parseDateArray(date: number[]): string {
  return `${date[0]}-${date[1].toString().padStart(2, '0')}-${date[2].toString().padStart(2, '0')} | ${date[3]}:${date[4].toString().padStart(2, '0')}`;
}
