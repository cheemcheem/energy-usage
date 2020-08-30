import React from "react";
import ErrorBoundary from "../../common/ErrorBoundary";

export default function MainContent({header, body, extraBodyClass}: { header: string, body?: React.ReactNode, extraBodyClass?: string }) {
  return <MainContentErrorBoundary>
    <div className="content content-rounded-border-box">
      <div className="content-header">
        <span>{header}</span>
      </div>
      <div className={`content-body ${extraBodyClass}`}>
        {body}
      </div>
    </div>
  </MainContentErrorBoundary>
}

function MainContentErrorBoundary(props: React.PropsWithChildren<any>) {
  return <ErrorBoundary renderError={<MainContent header={"Error Component Failed"}/>}>
    {props.children}
  </ErrorBoundary>
}