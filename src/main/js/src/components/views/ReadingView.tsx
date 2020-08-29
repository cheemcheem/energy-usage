import {ColDef, ColGroupDef} from "ag-grid-community";
import React, {useEffect, useMemo, useState} from "react";
import View, {
  configuredDateValueFormatter,
  configuredNumberValueFormatter,
  presetDateValueFormatter
} from "./View";

export default function ReadingView() {

  const numberFormatter = useMemo(() => configuredNumberValueFormatter("reading"), []);
  const dateTimeFormatOptions: Intl.DateTimeFormatOptions = useMemo(() => ({
    year: 'numeric', month: 'numeric', day: 'numeric',
    hour: 'numeric', minute: 'numeric', second: 'numeric',
    hour12: false
  }), []);
  const presetDateFormatter = useMemo(() => presetDateValueFormatter(dateTimeFormatOptions), [dateTimeFormatOptions]);
  const dateFormatter = useMemo(() => configuredDateValueFormatter(presetDateFormatter)("dateISO"), [presetDateFormatter]);

  const columnDefs: (ColGroupDef | ColDef)[] = [
    {
      headerName: "Date",
      field: "date",
      sortable: true,
      comparator: (valueA, valueB, nodeA, nodeB) => {
        const a = nodeA.data.dateISO;
        const b = nodeB.data.dateISO;
        return a - b;
      },
      minWidth: 200,
      valueFormatter: dateFormatter
    },
    {
      headerName: "Reading",
      field: "reading",
      sortable: true,
      minWidth: 100,
      valueFormatter: numberFormatter
    }
  ];

  const defaultColDef: ColDef = {
    resizable: true
  }

  const [rowData, setRowData] = useState([] as ({ dateISO: number, reading: number })[]);

  useEffect(() => {
    fetch("/api/reading/all")
    .then(response => response.text())
    .then(JSON.parse)
    .then((rows: ({ dateISO: string, reading: string })[]) =>
        rows.map(row => ({
          dateISO: Date.parse(row.dateISO),
          reading: Number.parseFloat(row.reading)
        })))
    .then(setRowData)
    .catch(console.error)
  }, [setRowData]);

  return <>
    <View title="Readings" columnDefs={columnDefs} rowData={rowData} defaultColDef={defaultColDef}/>
  </>;
}