import React from "react";
import {useState} from "react";
import {useCallback} from "react";
import Cookies from "js-cookie";

export default function NewPage() {
  const [date, setDate] = useState(new Date().toISOString().substring(0, 16));
  const [reading, setReading] = useState(0.00);
  const submitReading = useCallback(() => {
    const formattedDate = date.replace("T", " ") + ":00";
    fetch("/api/reading/add", {
      method: "POST",
      body: JSON.stringify({
        dateISO: formattedDate,
        reading
      }),
      headers: {
        "X-XSRF-TOKEN": Cookies.get("XSRF-TOKEN")!,
        "Content-Type": "application/json"
      }
    })
    .then(window.location.reload)
  }, [date, reading]);
  return <>
    <input id="dateInput" name="dateInput" type="datetime-local" value={date}
           onChange={({target}) => setDate(target.value)}/>
    <input id="readingInput" name="readingInput" type="number" value={reading} step={0.01}
           onChange={({target}) => setReading(Number(target.value))}/>
    <button onClick={submitReading}>Submit Reading</button>
  </>;
}