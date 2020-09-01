import React from "react";

type RegularProps = {
  url: string,
  dateField: string,
  dateFieldColumn: string,
  numberField: string,
  numberFieldColumn: string,
  dateTimeFormatOptions: Intl.DateTimeFormatOptions
};

export type ChartProps = RegularProps & { navigator?: { min: number, max: number } };

export type TableProps = RegularProps;

export type BaseContentProps = { body?: React.ReactNode, extraBodyClass?: string };
export type MainContentProps = { header: string, body?: React.ReactNode };
export type GroupContentProps = { header: string, chart: React.ReactNode, table: React.ReactNode, extraBodyClass?: string };