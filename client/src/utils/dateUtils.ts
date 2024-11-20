import { format, parse } from 'date-fns';

const DISPLAY_DATE_FORMAT = "yyyy/MM/dd";
const API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
const INPUT_DATE_FORMAT = "yyyy-MM-dd";

export const parseApiDate = (dateString: string): Date => {
    return parse(dateString, API_DATE_FORMAT, new Date());
}

export const parseInputDate = (dateString: string): Date => {
    return parse(dateString, INPUT_DATE_FORMAT, new Date());
}

export const formatForInput = (date: Date): string => {
    return format(date, INPUT_DATE_FORMAT);
}

export const formatForDisplay = (date: Date) : string => {
    return format(date, DISPLAY_DATE_FORMAT);
}

export const formatForApi = (date : Date) : string => {
    return format(date, INPUT_DATE_FORMAT);
}

export const parseCreationDate = (date: Date) : Date => {
    const withoutTime = formatForInput(date);
    return parseInputDate(withoutTime);
}