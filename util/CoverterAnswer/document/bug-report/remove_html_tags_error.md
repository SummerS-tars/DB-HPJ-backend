problem:

when converted from xml to csv  
many answers loss part of their content  

example1:

```xml
  <row Id="41924" PostTypeId="2" ParentId="18265" CreationDate="2008-09-03T15:22:20.570" Score="3" Body="&lt;p&gt;Here is an example of how to get some more info using a demangler. As you can see this one also logs the stacktrace to file.&lt;/p&gt;&#10;&#10;&lt;pre class=&quot;lang-c prettyprint-override&quot;&gt;&lt;code&gt;#include &amp;lt;iostream&amp;gt;&#10;#include &amp;lt;sstream&amp;gt;&#10;#include &amp;lt;string&amp;gt;&#10;#include &amp;lt;fstream&amp;gt;&#10;#include &amp;lt;cxxabi.h&amp;gt;&#10;&#10;void sig_handler(int sig)&#10;{&#10;    std::stringstream stream;&#10;    void * array[25];&#10;    int nSize = backtrace(array, 25);&#10;    char ** symbols = backtrace_symbols(array, nSize);&#10;    for (unsigned int i = 0; i &amp;lt; size; i++) {&#10;        int status;&#10;        char *realname;&#10;        std::string current = symbols[i];&#10;        size_t start = current.find(&quot;(&quot;);&#10;        size_t end = current.find(&quot;+&quot;);&#10;        realname = NULL;&#10;        if (start != std::string::npos &amp;amp;&amp;amp; end != std::string::npos) {&#10;            std::string symbol = current.substr(start+1, end-start-1);&#10;            realname = abi::__cxa_demangle(symbol.c_str(), 0, 0, &amp;amp;status);&#10;        }&#10;        if (realname != NULL)&#10;            stream &amp;lt;&amp;lt; realname &amp;lt;&amp;lt; std::endl;&#10;        else&#10;            stream &amp;lt;&amp;lt; symbols[i] &amp;lt;&amp;lt; std::endl;&#10;        free(realname);&#10;    }&#10;    free(symbols);&#10;    std::cerr &amp;lt;&amp;lt; stream.str();&#10;    std::ofstream file(&quot;/tmp/error.log&quot;);&#10;    if (file.is_open()) {&#10;        if (file.good())&#10;            file &amp;lt;&amp;lt; stream.str();&#10;        file.close();&#10;    }&#10;    signal(sig, &amp;amp;sig_handler);&#10;}&#10;&lt;/code&gt;&lt;/pre&gt;&#10;" OwnerUserId="22021" OwnerDisplayName="AndersO" LastEditorUserId="906523" LastEditDate="2016-04-13T17:10:29.337" LastActivityDate="2016-04-13T17:10:29.337" CommentCount="2" ContentLicense="CC BY-SA 3.0" />
```

was converted to the csv like  

```csv
"18265","Here is an example of how to get some more info using a demangler. As you can see this one also logs the stacktrace to file. #include #include #include #include #include void sig_handler(int sig) { std::stringstream stream; void * array[25]; int nSize = backtrace(array, 25); char ** symbols = backtrace_symbols(array, nSize); for (unsigned int i = 0; i","41924","3"
```

example2:

```xml
  <row Id="30257" PostTypeId="2" ParentId="2898" CreationDate="2008-08-27T14:21:43.050" Score="133" Body="&lt;p&gt;&lt;img src=&quot;https://i.stack.imgur.com/YayAV.png&quot; alt=&quot;Real programmers set the universal constants at the start such that the universe evolves to contain the disk with the data they want.&quot;&gt;&lt;/p&gt;&#10;&#10;&lt;p&gt;&lt;a href=&quot;http://xkcd.com/378/&quot; rel=&quot;noreferrer&quot;&gt;http://xkcd.com/378/&lt;/a&gt;&lt;/p&gt;&#10;" OwnerUserId="253" OwnerDisplayName="Scott Cowan" LastEditorUserId="1219121" LastEditDate="2012-03-30T04:51:44.030" LastActivityDate="2012-03-30T04:51:44.030" CommentCount="2" CommunityOwnedDate="2009-05-03T11:37:34.303" ContentLicense="CC BY-SA 3.0" />
```

was converted to the csv like

```csv
"2898","http://xkcd.com/378/","30257","133"
```
