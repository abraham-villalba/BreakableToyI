/**
 * dateUtils.ts
 * @file Utility functions for date formatting and parsing
 */
import { format, parse } from 'date-fns';

const DISPLAY_DATE_FORMAT = "yyyy/MM/dd";
const API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
const INPUT_DATE_FORMAT = "yyyy-MM-dd";

/**
 * Parses a date string from the API into a Date object
 * @param dateString - The date string to parse
 * @returns The parsed Date object
 */
export const parseApiDate = (dateString: string): Date => {
    return parse(dateString, API_DATE_FORMAT, new Date());
}

/**
 * Parses a date string from the input field into a Date object
 * @param dateString - The date string to parse
 * @returns The parsed Date object
 */
export const parseInputDate = (dateString: string): Date => {
    return parse(dateString, INPUT_DATE_FORMAT, new Date());
}

/**
 * Formats a Date object into a string for an input field
 * @param date - The Date object to format
 * @returns The formatted date string
 */
export const formatForInput = (date: Date): string => {
    return format(date, INPUT_DATE_FORMAT);
}

/**
 * Formats a Date object into a string for display
 * @param date - The Date object to format
 * @returns The formatted date string
 */
export const formatForDisplay = (date: Date) : string => {
    return format(date, DISPLAY_DATE_FORMAT);
}

/**
 * Formats a Date object into a string for the API
 * @param date - The Date object to format
 * @returns The formatted date string
 */
export const formatForApi = (date : Date) : string => {
    return format(date, INPUT_DATE_FORMAT);
}

/**
 * Parses a date string from the API into a Date object
 * @param dateString - The date string to parse
 * @returns The parsed Date object
 */
export const parseCreationDate = (date: Date) : Date => {
    const withoutTime = formatForInput(date);
    return parseInputDate(withoutTime);
}