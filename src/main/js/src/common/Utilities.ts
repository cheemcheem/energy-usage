import {ValueFormatterParams} from "ag-grid-community";

export const presetNumberValueFormatter = new Intl.NumberFormat("en-GB", {
  style: "currency",
  currency: "GBP"
});
export const configuredNumberValueFormatter =
    (paramName: string) =>
        (value: ValueFormatterParams) =>
            presetNumberValueFormatter.format(value.data[paramName]);
export const presetDateValueFormatter =
    (options: Intl.NumberFormatOptions) =>
        new Intl.DateTimeFormat('en-GB', options);
export const configuredDateValueFormatter =
    (presetDateValueFormatter: Intl.DateTimeFormat) =>
        (paramName: string) =>
            (value: ValueFormatterParams) =>
                presetDateValueFormatter.format(value.data[paramName]);