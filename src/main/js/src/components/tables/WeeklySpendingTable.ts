import Table from "../subcomponents/Table";
import {useMemo} from "react";

export default function WeeklySpendingTable() {
  return Table(useMemo(() => ({
    title: "Weekly Spending",
    url: "/api/spending/weekly/all",
    dateField: "startDateISO",
    dateFieldColumn: "Week",
    numberField: "usage",
    numberFieldColumn: "Weekly Spending",
    dateTimeFormatOptions: {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    }
  }), []));
}