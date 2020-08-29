import React, {useEffect, useMemo, useState} from "react";
import {ColDef, ColGroupDef} from "ag-grid-community";
import View, {
  configuredDateValueFormatter,
  configuredNumberValueFormatter,
  presetDateValueFormatter
} from "./View";

export default function SpendingView() {

  const numberFormatter = useMemo(() => configuredNumberValueFormatter("usage"), []);
  const dateTimeFormatOptions: Intl.DateTimeFormatOptions = useMemo(() => ({
    year: 'numeric',
    month: 'long',
  }), []);
  const presetDateFormatter = useMemo(() => presetDateValueFormatter(dateTimeFormatOptions), [dateTimeFormatOptions]);
  const startDateFormatter = useMemo(() => configuredDateValueFormatter(presetDateFormatter)("startDateISO"), [presetDateFormatter]);

  const columnDefs: (ColGroupDef | ColDef)[] = useMemo(() => ([

    {
      headerName: "Month",
      field: "startDateISO",
      sortable: true,
      comparator: (valueA, valueB, nodeA, nodeB) => {
        const a = nodeA.data.startDateISO;
        const b = nodeB.data.startDateISO;
        return a - b;
      },
      minWidth: 150,
      valueFormatter: startDateFormatter
    }
    ,
    {
      headerName: "Daily Usage",
      field: "usage",
      sortable: true,
      minWidth: 100,
      valueFormatter: numberFormatter
    }
  ]), [numberFormatter, startDateFormatter]);

  const defaultColDef: ColDef = useMemo(() => ({resizable: true}), []);


  const [rowData, setRowData] = useState([] as ({ startDateISO: number, endDateISO: number, usage: number })[]);

  useEffect(() => {
    fetch("/api/average/monthly/all")
    .then(response => response.text())
    .then(JSON.parse)
    .then((rows: ({ startDateISO: string, endDateISO: string, usage: string })[]) =>
        rows.map(row => ({
          startDateISO: Date.parse(row.startDateISO),
          endDateISO: Date.parse(row.endDateISO),
          usage: Number.parseFloat(row.usage)
        })))
    .then(setRowData)
    .catch(console.error)
  }, [setRowData]);

  return <>
    <View title="Monthly Usage" columnDefs={columnDefs} rowData={rowData}
          defaultColDef={defaultColDef}/>
  </>;
}