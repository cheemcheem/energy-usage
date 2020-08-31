import React from "react";
import GroupContent from "../subcomponents/GroupContent";
import MonthlySpendingChart from "../charts/MonthlySpendingChart";
import MonthlySpendingTable from "../tables/MonthlySpendingTable";

export default function MonthlySpendingGroup() {
  return <GroupContent header={"Monthly Spending"}
                       chart={<MonthlySpendingChart/>}
                       table={<MonthlySpendingTable/>}
  />
}