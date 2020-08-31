import React, {useContext, useEffect, useMemo, useState} from "react";
import ContentBody from "./ContentBody";
import {AgChartsReact} from "ag-charts-react";
import {ChartProps} from "../../common/Props";
import {DarkModeContext} from "../../contexts/DarkModeContext";
import {presetDateValueFormatter} from "../../common/Utilities";

export default function Chart(props: ChartProps) {

  const {isDarkMode} = useContext(DarkModeContext);

  const {dateField, dateFieldColumn, dateTimeFormatOptions, numberField, numberFieldColumn, url, navigator} = useMemo(() => props, [props]);

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

  const mainColour = useMemo(() => isDarkMode ? "#FFFFFF" : "#000000", [isDarkMode]);
  const backgroundColour = useMemo(() => isDarkMode ? "#000000" : "#FFFFFF", [isDarkMode]);

  const options = useMemo(() => ({
    data,
    autoSize: true,
    series: [{
      type: 'column',
      xKey: dateField,
      xName: dateFieldColumn,
      yKeys: [numberField],
      yNames: [numberFieldColumn]
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
        label: {formatter: ({value}: any) => `£${value}`, color: mainColour},
        line: {color: mainColour},
        tick: {color: mainColour},
        gridStyle: [{stroke: '#333333', lineDash: [1, 0]}]
      },
      {
        title: {
          enabled: false,
          text: dateFieldColumn
        },
        type: 'category',
        position: 'bottom',
        label: {rotation: 45, color: mainColour},
        line: {color: mainColour},
        tick: {color: mainColour},
        gridStyle: [{lineDash: [1, 0]}]
      }
    ],
    navigator: {
      enabled: true,
      height: 30,
      min: navigator?.min ?? 0.30,
      max: navigator?.max ?? 0.80
    },
    background: {
      fill: backgroundColour
    }
  }), [backgroundColour, data, dateField, dateFieldColumn, mainColour, navigator, numberField, numberFieldColumn]);

  // don't render unless ready, otherwise navigator won't work by default
  return <ContentBody body={
    data.length > 0
        ? <AgChartsReact options={options}/>
        : <></>
  }/>;
}