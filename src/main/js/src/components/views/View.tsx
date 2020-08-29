import React, {useContext} from "react";
import {DarkModeContext} from "../../contexts/DarkModeContext";
import {AgGridReact} from "ag-grid-react";
import {ColDef, ColGroupDef, ValueFormatterParams} from "ag-grid-community";

export default function View({title, columnDefs, rowData, defaultColDef}: {
  title: string,
  columnDefs: (ColDef | ColGroupDef)[],
  rowData: any[],
  defaultColDef: ColDef
}) {
  const {isDarkMode} = useContext(DarkModeContext);
  return <>
    <div className="content content-rounded-border-box">
      <div className="content-header">
        <span>{title}</span>
      </div>
      <div className={`ag-theme-alpine${isDarkMode ? "-dark" : ""} content-body`}>
        <AgGridReact
            defaultColDef={defaultColDef}
            columnDefs={columnDefs}
            rowData={rowData}
            onFirstDataRendered={event => event.api.sizeColumnsToFit()}
        />
      </div>
    </div>
  </>;
}
const presetNumberValueFormatter = new Intl.NumberFormat("en-GB", {
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
