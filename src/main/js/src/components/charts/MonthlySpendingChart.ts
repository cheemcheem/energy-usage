import {useMemo} from "react";
import Chart from "../subcomponents/Chart";

export default function MonthlySpendingChart() {
  return Chart(useMemo(() => ({
    title: "Monthly Spending Chart",
    url: "/api/spending/monthly/all",
    dateField: "startDateISO",
    dateFieldColumn: "Month",
    numberField: "usage",
    numberFieldColumn: "Spending",
    dateTimeFormatOptions: {
      year: 'numeric',
      month: 'long',
    },
    navigator: {
      min: 0.00,
      max: 0.50
    }
  }), []));
}