import Table from "../subcomponents/Table";
import {useMemo} from "react";

export default function WeeklySpendingTable() {
  return Table(useMemo(() => ({
    title: "Weekly Usage",
    url: "/api/spending/weekly/all",
    dateField: "startDateISO",
    dateFieldColumn: "Week",
    numberField: "usage",
    numberFieldColumn: "Weekly Usage",
    dateTimeFormatOptions: {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    }
  }), []));
}