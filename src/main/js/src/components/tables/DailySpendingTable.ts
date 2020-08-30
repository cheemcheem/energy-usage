import Table from "../subcomponents/Table";
import {useMemo} from "react";

export default function DailySpendingTable() {
  return Table(useMemo(() => ({
    title: "Daily Spending",
    url: "/api/spending/daily/all",
    dateField: "startDateISO",
    dateFieldColumn: "Date",
    numberField: "usage",
    numberFieldColumn: "Spending",
    dateTimeFormatOptions: {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    }
  }), []));
}