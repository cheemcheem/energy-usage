import React from "react";
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-alpine.css';
import 'ag-grid-community/dist/styles/ag-theme-alpine-dark.css';

import Page from "./Page";
import ReadingTable from "./tables/ReadingTable";
import MonthlySpendingTable from "./tables/MonthlySpendingTable";
import DailySpendingTable from "./tables/DailySpendingTable";
import WeeklySpendingTable from "./tables/WeeklySpendingTable";
import {DarkModeContext} from "../contexts/DarkModeContext";
import useMediaQuery from "../hooks/useMediaQuery";
import DailySpendingChart from "./charts/DailySpendingChart";
import WeeklySpendingChart from "./charts/WeeklySpendingChart";
import MonthlySpendingChart from "./charts/MonthlySpendingChart";

export default function MainPage({fullName, userName, logout}: { fullName: string, userName: string, logout: () => any }) {
  const isDarkMode = useMediaQuery("(prefers-color-scheme: dark)", false);
  return <Page header={<>
    <span>
      <span>Welcome </span>
      <span className="tooltip">{fullName}
        <div className={"tooltip-text header-inner header-inner-with-border"}>
          <a href={`https://github.com/${userName}`}
             target="_blank"
             rel="noopener noreferrer">GitHub: {userName}</a>
        </div>
      </span>
    </span>
    <button className="header-inner header-inner-with-border" onClick={logout}>Logout</button>
  </>} body={<DarkModeContext.Provider value={{isDarkMode}}>
    <DailySpendingChart/>
    <WeeklySpendingChart/>
    <MonthlySpendingChart/>
    <ReadingTable/>
    <DailySpendingTable/>
    <WeeklySpendingTable/>
    <MonthlySpendingTable/>
  </DarkModeContext.Provider>}/>;
}