import React, {useMemo} from "react";
import ErrorBoundary from "../../common/ErrorBoundary";
import {GroupContentProps} from "../../common/Props";
import ContentBody from "./ContentBody";
import useSavedState from "../../hooks/useSavedState";

export default function GroupContent(props: GroupContentProps) {
  const {header, chart, table, extraBodyClass} = props;
  const [visibleElement, setVisibleElement] = useSavedState("table" as "chart" | "table", `${header}-group-visible`);

  const setValue = useMemo(() =>
          (newVisibleElement: "chart" | "table") => () => setVisibleElement(newVisibleElement),
      [setVisibleElement]);

  return <GroupContentErrorBoundary>
    <div className="content content-rounded-border-box">
      <div className="content-header content-header-group">
        <span>{header}</span>
        <div style={{borderLeft: "solid grey 2px"}} className={"content-header-group"}>
          <span className={visibleElement === "chart" ? "content-body-selectable" : ""}
                onClick={setValue("table")}>Table</span>
          <span className={visibleElement === "table" ? "content-body-selectable" : ""}
                onClick={setValue("chart")}>Chart</span>
        </div>
      </div>
      <div style={{display: visibleElement === "chart" ? "unset" : "none"}}
           className={`content-body ${extraBodyClass}`}>
        {chart}
      </div>
      <div style={{display: visibleElement === "table" ? "unset" : "none"}}
           className={`content-body ${extraBodyClass}`}>
        {table}
      </div>
    </div>
  </GroupContentErrorBoundary>
}

function GroupContentErrorBoundary(props: React.PropsWithChildren<any>) {
  return <ErrorBoundary renderError={<ContentBody/>}>
    {props.children}
  </ErrorBoundary>
}