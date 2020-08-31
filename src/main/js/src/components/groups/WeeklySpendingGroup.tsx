import React from "react";
import GroupContent from "../subcomponents/GroupContent";
import WeeklySpendingChart from "../charts/WeeklySpendingChart";
import WeeklySpendingTable from "../tables/WeeklySpendingTable";

export default function WeeklySpendingGroup() {
  return <GroupContent header={"Weekly Spending"}
                       chart={<WeeklySpendingChart/>}
                       table={<WeeklySpendingTable/>}
  />
}