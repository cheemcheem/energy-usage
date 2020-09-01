import React from "react";
import GroupContent from "../subcomponents/GroupContent";
import DailySpendingChart from "../charts/DailySpendingChart";
import DailySpendingTable from "../tables/DailySpendingTable";

export default function DailySpendingGroup() {
  return <GroupContent header={"Daily Spending"}
                       chart={<DailySpendingChart/>}
                       table={<DailySpendingTable/>}
  />
}