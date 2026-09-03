
  # Security Policy

  ## Supported Versions

  Only the latest release of Angry IP Scanner receives security fixes.

  | Version        | Supported |
  |----------------|-----------|
  | 3.9.3 (latest) | Yes       |
  | < 3.9.3        | No        |

 
  ## Reporting a Vulnerability

  Please **do not** open a public GitHub issue for security vulnerabilities.

  **Preferred channel — GitHub Private Advisory:**
  Use the **"Report a vulnerability"** button on the
  [Security tab](https://github.com/angryip/ipscan/security) of this
  repository. This keeps the report confidential until a fix is released
  and allows GitHub to assign a CVE on request.

  > **Note for maintainers:** If the "Report a vulnerability" button is not
  > visible, enable **Private vulnerability reporting** under
  > Settings → Code security and analysis.

  **Alternative — Email:**
  Contact the maintainer directly at `[maintainer email]` if the button
  above is unavailable.


  ## What to Include

  - Description of the vulnerability and its potential impact
  - Affected version(s)
  - Steps to reproduce, or a proof-of-concept
  - Suggested remediation (optional but welcome)


  ## Response Timeline

  | Milestone                   | Target  |
  |-----------------------------|---------|
  | Acknowledgement of receipt  | 14 days |
  | Initial triage              | 30 days |
  | Fix or remediation plan     | 90 days |

  We follow a **90-day coordinated disclosure** window from the date of
  acknowledgement. Please allow this time before publishing vulnerability
  details publicly.

  If no acknowledgement is received within 14 days, please follow up by
  email or escalate to [MITRE](https://cveform.mitre.gov/).


  ## Scope

  - Vulnerabilities in Angry IP Scanner application code
  - Security issues in exported file formats (SQL, XML, CSV) that cause harm
    when the exported file is imported into a database or other tool
  - Vulnerabilities in bundled third-party dependencies


  ## Out of Scope

  - Issues requiring physical access to the user's machine
  - Vulnerabilities in operating system or JVM components not shipped with
    the application
