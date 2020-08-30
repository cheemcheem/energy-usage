import React, {useMemo} from "react";
import Page from "./Page";

export default function LoginPage() {
  const href = useMemo(() => {
    let port = (window.location.port ? ':' + window.location.port : '');
    // handle localhost dev case
    if (port === ':3000') {
      port = ':8080';
    }
    return window.location.protocol + '//' + window.location.hostname + port + '/api/private';
  }, []);

  return (
      <Page header="Welcome"
            body={<>
              <div id="login-container" className={"content-rounded-border-box content"}>
                <h1>Please log in to continue</h1>
                <a href={href} className="content-rounded-border-box content-inner">
                  <span>Log in with GitHub (OAuth2)</span>
                </a>
              </div>
            </>}
      />
  );
}