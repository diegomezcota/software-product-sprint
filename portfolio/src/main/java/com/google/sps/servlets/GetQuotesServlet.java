// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/** Handle requests for hello-world page, sends greeting. */
@WebServlet("/get-message")
public class GetQuotesServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<String> sHarryPotterQuotes = new ArrayList<String>();
    sHarryPotterQuotes.add("\"Yer a wizard Harry.\" - Rubeus Hagrid");
    sHarryPotterQuotes.add("\"Dobby is free.\" - Dobby");
    sHarryPotterQuotes.add("\"I solemnly swear I am up to no good.\" - Harry Potter");
    sHarryPotterQuotes.add("\"Things we lose have a way of coming back to us in the end, if not always in the way we expect.\" - Luna Lovegood.");
    sHarryPotterQuotes.add("\"It is the quality of one's convictions that determines success, not the number of followers.\" - Remus Lupin");
    sHarryPotterQuotes.add("\"I've always wanted to use that spell.\" - Minerva McGonagall");
    sHarryPotterQuotes.add("'After all this time?' 'Always,' said Snape.");
    sHarryPotterQuotes.add("\"Numbing the pain for a while will make it worse when you finally feel it.\" - Albus Dumbledore");
    // Convert quotes to json format
    Gson gson = new Gson();
    String json = gson.toJson(sHarryPotterQuotes);
    // Return server response as JSON
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
