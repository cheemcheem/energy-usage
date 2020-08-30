import Table from "../subcomponents/Table";
import {useMemo} from "react";

export default function DailySpendingTable() {
  return Table(useMemo(() => ({
    title: "Daily Usage",
    url: "/api/average/daily/all",
    dateField: "startDateISO",
    dateFieldColumn: "Date",
    numberField: "usage",
    numberFieldColumn: "Usage",
    dateTimeFormatOptions: {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    }
  }), []));
}