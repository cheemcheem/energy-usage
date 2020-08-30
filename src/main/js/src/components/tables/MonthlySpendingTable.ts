import Table from "../subcomponents/Table";
import {useMemo} from "react";

export default function MonthlySpendingTable() {
  return Table(useMemo(() => ({
    title: "Monthly Spending",
    url: "/api/spending/monthly/all",
    dateField: "startDateISO",
    dateFieldColumn: "Month",
    numberField: "usage",
    numberFieldColumn: "Monthly Spending",
    dateTimeFormatOptions: {
      year: 'numeric',
      month: 'long',
    }
  }), []));
}