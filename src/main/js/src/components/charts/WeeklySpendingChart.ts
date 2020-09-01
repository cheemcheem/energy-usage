import {useMemo} from "react";
import Chart from "../subcomponents/Chart";

export default function WeeklySpendingChart() {
  return Chart(useMemo(() => ({
    title: "Weekly Spending Chart",
    url: "/api/spending/weekly/all",
    dateField: "startDateISO",
    dateFieldColumn: "Week",
    numberField: "usage",
    numberFieldColumn: "Spending",
    dateTimeFormatOptions: {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    },
    navigator: {
      min: 0.00,
      max: 0.20
    }
  }), []));
}