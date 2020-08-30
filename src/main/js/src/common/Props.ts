type RegularProps = {
  title: string,
  url: string,
  dateField: string,
  dateFieldColumn: string,
  numberField: string,
  numberFieldColumn: string,
  dateTimeFormatOptions: Intl.DateTimeFormatOptions
};

export type ChartProps = RegularProps & { navigator?: { min: number, max: number } };

export type TableProps = RegularProps;