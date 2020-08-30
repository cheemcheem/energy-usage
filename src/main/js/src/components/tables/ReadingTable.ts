import Table from "../subcomponents/Table";
import {useMemo} from "react";

export default function ReadingTable() {
  return Table(useMemo(() => ({
    title: "Readings",
    url: "/api/reading/all",
    dateField: "dateISO",
    dateFieldColumn: "Date",
    numberField: "reading",
    numberFieldColumn: "Reading",
    dateTimeFormatOptions: {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
      second: 'numeric',
      hour12: false
    }
  }), []));
}