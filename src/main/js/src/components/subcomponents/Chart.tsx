import React, {useEffect, useMemo, useState} from "react";
import MainContent from "../subcomponents/MainContent";
import {AgChartsReact} from "ag-charts-react";
import {presetDateValueFormatter} from "./Table";
import {RegularProps} from "../../common/Props";

export default function Chart(props: RegularProps & { navigator?: { min: number, max: number } }) {

  const {dateField, dateFieldColumn, dateTimeFormatOptions, numberField, numberFieldColumn, title, url, navigator} = useMemo(() => props, [props]);

  const presetDateFormatter = useMemo(
      () => presetDateValueFormatter(dateTimeFormatOptions),
      [dateTimeFormatOptions]);

  const [data, setData] = useState([] as any[]);

  useEffect(() => {
    fetch(url)
    .then(response => response.text())
    .then(JSON.parse)
    .then((rows: any[]) => rows.map(row => {
      const newVal: any = {};
      newVal[numberField] = Number(row[numberField]);
      newVal[dateField] = presetDateFormatter.format(new Date(row[dateField]));
      return newVal;
    }))
    .then(setData)
    .catch(console.error)
  }, [url, numberField, dateField, setData, presetDateFormatter]);


  const options = {
    data,
    title: {
      enabled: false,
      text: title
    },
    autoSize: true,
    series: [{
      type: 'column',
      xKey: dateField,
      xName: dateFieldColumn,
      yKeys: [numberField],
      yNames: [numberFieldColumn],
      fills: ['blue'],
      fillOpacity: 0.5
    }],
    legend: {enabled: false},
    axes: [
      {
        title: {
          enabled: false,
          text: numberFieldColumn
        },
        type: 'number',
        position: 'left',
        label: {formatter: ({value}: any) => `£${value}`}
      },
      {
        title: {
          enabled: false,
          text: dateFieldColumn
        },
        type: 'category',
        position: 'bottom',
        label: {rotation: 45}
      }
    ],
    navigator: {
      enabled: true,
      height: 30,
      min: navigator?.min ?? 0.30,
      max: navigator?.max ?? 0.80
    }
  }

  // don't render unless ready, otherwise navigator won't work by default
  return <MainContent header={title} body={
    data.length > 0
        ? <AgChartsReact options={options}/>
        : <></>
  }/>;
}