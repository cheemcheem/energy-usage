import React, {useContext, useEffect, useMemo, useState} from "react";
import {DarkModeContext} from "../../contexts/DarkModeContext";
import {AgGridReact} from "ag-grid-react";
import {ColDef, ColGroupDef} from "ag-grid-community";
import MainContent from "./MainContent";
import {TableProps} from "../../common/Props";
import {
  configuredDateValueFormatter,
  configuredNumberValueFormatter,
  presetDateValueFormatter
} from "../../common/Utilities";

export default function Table(props: TableProps) {
  const {dateField, dateFieldColumn, dateTimeFormatOptions, numberField, numberFieldColumn, title, url} = props;
  const {isDarkMode} = useContext(DarkModeContext);
  const configuredNumberFormatter = useMemo(
      () => configuredNumberValueFormatter(numberField),
      [numberField]
  );

  const presetDateFormatter = useMemo(
      () => presetDateValueFormatter(dateTimeFormatOptions),
      [dateTimeFormatOptions]);

  const configuredDateFormatter = useMemo(
      () => configuredDateValueFormatter(presetDateFormatter)(dateField),
      [presetDateFormatter, dateField]
  );

  const columnDefs: (ColGroupDef | ColDef)[] = useMemo(() => ([
    {
      headerName: dateFieldColumn,
      field: dateField,
      sortable: true,
      comparator: (valueA, valueB, nodeA, nodeB) => nodeA.data[dateField] - nodeB.data[dateField],
      minWidth: 150,
      valueFormatter: configuredDateFormatter
    },
    {
      headerName: numberFieldColumn,
      field: numberField,
      sortable: true,
      minWidth: 100,
      valueFormatter: configuredNumberFormatter
    }
  ] as (ColGroupDef | ColDef)[]), [dateField, dateFieldColumn, numberField, numberFieldColumn, configuredNumberFormatter, configuredDateFormatter]);

  const defaultColDef: ColDef = useMemo(() => ({resizable: true}), []);

  const [rowData, setRowData] = useState([] as any[]);
  useEffect(() => {
    fetch(url)
    .then(response => response.text())
    .then(JSON.parse)
    .then((rows: any[]) => rows.map(row => {
      const newVal: any = {};
      newVal[numberField] = row[numberField];
      newVal[dateField] = new Date(row[dateField]);
      return newVal;
    }))
    .then(setRowData)
    .catch(console.error)
  }, [url, numberField, dateField, setRowData]);

  return <MainContent
      header={title}
      body={<AgGridReact
          defaultColDef={defaultColDef}
          columnDefs={columnDefs}
          rowData={rowData}
          onFirstDataRendered={event => event.api.sizeColumnsToFit()}
      />}
      extraBodyClass={`ag-theme-alpine${isDarkMode ? "-dark" : ""}`}
  />;
}