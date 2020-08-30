import {useMemo} from "react";
import Chart from "../subcomponents/Chart";

export default function DailySpendingChart() {
  return Chart(useMemo(() => ({
    title: "Daily Spending Chart",
    url: "/api/spending/daily/all",
    dateField: "startDateISO",
    dateFieldColumn: "Day",
    numberField: "usage",
    numberFieldColumn: "Spending",
    dateTimeFormatOptions: {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    },
    navigator: {
      min: 0.00,
      max: 0.01
    }
  }), []));
}