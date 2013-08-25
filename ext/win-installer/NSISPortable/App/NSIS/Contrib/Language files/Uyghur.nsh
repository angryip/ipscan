;Language: Uyghur (1152)
;Uyghur(China), Translated By Yasinjan Ghupur (yasenghupur@sina.com)

!insertmacro LANGFILE "Uyghur" "Uyghur"

!ifdef MUI_WELCOMEPAGE
  ${LangFileString} MUI_TEXT_WELCOME_INFO_TITLE " $(^NameDA) نى قاچىلاش يېتەكچىسىنى ئىشلىتىشىڭىزنى قارشى ئالىدۇ"
  ${LangFileString} MUI_TEXT_WELCOME_INFO_TEXT "قاچىلاش يېتەكچىسى $(^NameDA)نى قاچىلاش جەريانىغا يېتەكچىلىك قىلىدۇ  .$\r$\n$\r$\nقاچىلاشتىن بۇرۇن بارلىق پروگراممىلارنى يېپىۋېتىشنى تەۋسىيە قىلىدۇ،بۇنىڭ بىلەن قاچىلاپ بولغاندىن كېيىن كومپيۇتېرىڭىزنى قايتا قوزغاتمىسىڭىزمۇ بولىدۇ.$\r$\n$\r$\n$_CLICK"
!endif

!ifdef MUI_UNWELCOMEPAGE
  ${LangFileString} MUI_UNTEXT_WELCOME_INFO_TITLE " $(^NameDA) نى ئۆچۈرۈش يېتەكچىسىنى ئىشلىتىشىڭىزنى قارشى ئالىدۇ"
  ${LangFileString} MUI_UNTEXT_WELCOME_INFO_TEXT "ئۆچۈرۈش يېتەكچىسى$(^NameDA)نى ئۆچۈرۈش جەريانىغا يېتەكچىلىك قىلىدۇ   .$\r$\n$\r$\n ئۆچۈرۈشتىن بۇرۇن $(^NameDA)نىڭ قوزغىتىلمىغانلىقىنى جەزىملەشتۈرۈڭ.$\r$\n$\r$\n$_CLICK"
!endif

!ifdef MUI_LICENSEPAGE
  ${LangFileString} MUI_TEXT_LICENSE_TITLE "ئىجازەت كېلىشىمى"
  ${LangFileString} MUI_TEXT_LICENSE_SUBTITLE "$(^NameDA)نى قاچىلىشتىن بۇرۇن  ئىجازەت تۈرلىرىنى كۆرۈپ چىقىڭ ."
  ${LangFileString} MUI_INNERTEXT_LICENSE_BOTTOM "$(^NameDA)نى قاچىلىش ئۈچۈن،كېلىشىم تۈرلىرىگە قوشۇلىشىڭىز كېرەك. قوشۇلسىڭىز،قوشۇلىمەن نى بېسىپ داۋاملاشتۇرۇڭ ."
  ${LangFileString} MUI_INNERTEXT_LICENSE_BOTTOM_CHECKBOX  "$(^NameDA)نى قاچىلاش ئۈچۈن كېلىشىمگە قوشۇلىشىڭىز كېرەك   كېلىشىم تۈرلىرىگە قوشۇلسىڭىز،تاللاش كاتىكىنى بېسىڭ. $_CLICK"
  ${LangFileString} MUI_INNERTEXT_LICENSE_BOTTOM_RADIOBUTTONS "$(^NameDA)نى قاچىلىش ئۈچۈن،كېلىشىم تۈرلىرىگە قوشۇلىشىڭىز كېرەك. قوشۇلسىڭىز،بىرىنجى تاللاشنى تاللاڭ. $_CLICK"
!endif

!ifdef MUI_UNLICENSEPAGE
  ${LangFileString} MUI_UNTEXT_LICENSE_TITLE "ئىجازەت كېلىشىمى"
  ${LangFileString} MUI_UNTEXT_LICENSE_SUBTITLE " $(^NameDA)نى ئۆچۈرۈشتىن بۇرۇن ئىجازەت كېلىشىم تۈرلىرىنى كۆرۈپ چىقىڭ ."
  ${LangFileString} MUI_UNINNERTEXT_LICENSE_BOTTOM "$(^NameDA)نى ئۆچۈرۈش ئۈچۈن، كېلىشىمگە قوشۇلىشىڭىز كېرەك .كېلىشىم تۈرلىرىگە قوشۇلسىڭىز, قوشۇلىمەن نى بېسىپ داۋاملاشتۇرۇڭ ."
  ${LangFileString} MUI_UNINNERTEXT_LICENSE_BOTTOM_CHECKBOX "$(^NameDA) نى ئۆچۈرۈش  ئۈچۈن كېلىشىم تۈرلىرىگە قوشۇلىشىڭىز كېرەك .قوشۇلسىڭىز, تاللاش كاتەكچىسىنى چېكىڭ. $_CLICK"
  ${LangFileString} MUI_UNINNERTEXT_LICENSE_BOTTOM_RADIOBUTTONS "$(^NameDA)نى ئۆچۈرۈش ئۈچۈن كېلىشىم تۈرلىرىگە قوشۇلىشىڭىز كېرەك. قوشۇلسىڭىز،بىرىنجى تاللاشنى تاللاڭ . $_CLICK"
!endif

!ifdef MUI_LICENSEPAGE | MUI_UNLICENSEPAGE
  ${LangFileString} MUI_INNERTEXT_LICENSE_TOP "كېلىشىمنىڭ قالغان قىسمىنى كۆرۈش ئۈچۈن Page Down نى بېسىڭ  ."
!endif

!ifdef MUI_COMPONENTSPAGE
  ${LangFileString} MUI_TEXT_COMPONENTS_TITLE "بۆلەك تاللاش"
  ${LangFileString} MUI_TEXT_COMPONENTS_SUBTITLE "$(^NameDA) نى قاچىلاش ئۈچۈن ،ئېھتىياجىلىق ئىقتىدارلارنى تاللاڭ"
!endif

!ifdef MUI_UNCOMPONENTSPAGE
  ${LangFileString} MUI_UNTEXT_COMPONENTS_TITLE "بۆلەك تاللاش"
  ${LangFileString} MUI_UNTEXT_COMPONENTS_SUBTITLE "$(^NameDA) نى ئۆچۈرۈش ئۈچۈن ،ئېھتىياجىلىق ئىقتىدارلارنى تاللاڭ."
!endif

!ifdef MUI_COMPONENTSPAGE | MUI_UNCOMPONENTSPAGE
  ${LangFileString} MUI_INNERTEXT_COMPONENTS_DESCRIPTION_TITLE "چۈشەندۈرۈش"
  !ifndef NSIS_CONFIG_COMPONENTPAGE_ALTERNATIVE
    ${LangFileString} MUI_INNERTEXT_COMPONENTS_DESCRIPTION_INFO "بۆلەكنىڭ چۈشەندۈرۈشىنى كۆرۈش ئۈچۈن،مائوسنى بۆلەكنىڭ ئۈستىگە ئاپىرىڭ."
  !else
    ${LangFileString} MUI_INNERTEXT_COMPONENTS_DESCRIPTION_INFO "بۆلەكنىڭ چۈشەندۈرۈشىنى كۆرۈش ئۈچۈن،مائوسنى بۆلەكنىڭ ئۈستىگە ئاپىرىڭ."
  !endif
!endif

!ifdef MUI_DIRECTORYPAGE
  ${LangFileString} MUI_TEXT_DIRECTORY_TITLE "قاچىلايدىغان ئورۇننى تاللاڭ"
  ${LangFileString} MUI_TEXT_DIRECTORY_SUBTITLE "$(^NameDA)نى قاچىلاش ئۈچۈن قاچىلايدىغان قىسقۇچنى تاللاڭ."
!endif

!ifdef MUI_UNDIRECTORYPAGE
  ${LangFileString} MUI_UNTEXT_DIRECTORY_TITLE " ئۆچۈرۈدىغان ئورۇننى تاللاڭ"
  ${LangFileString} MUI_UNTEXT_DIRECTORY_SUBTITLE "$(^NameDA)نى ئۆچۈرۈش ئۈچۈن ئۆچۈرۈدىغان قىسقۇچنى تاللاڭ."
!endif

!ifdef MUI_INSTFILESPAGE
  ${LangFileString} MUI_TEXT_INSTALLING_TITLE "قاچىلاۋاتىدۇ"
  ${LangFileString} MUI_TEXT_INSTALLING_SUBTITLE "$(^NameDA)نى قاچىلاۋاتىدۇ...تەخىر قىلىڭ  ."
  ${LangFileString} MUI_TEXT_FINISH_TITLE "قاچىلاش تاماملاندى"
  ${LangFileString} MUI_TEXT_FINISH_SUBTITLE "مۇۋەپپىقىيەتلىك قاچىلاندى."
  ${LangFileString} MUI_TEXT_ABORT_TITLE "قاچىليالمىدى"
  ${LangFileString} MUI_TEXT_ABORT_SUBTITLE "قاچىلاش مەغلۇپ بولدى."
!endif

!ifdef MUI_UNINSTFILESPAGE
  ${LangFileString} MUI_UNTEXT_UNINSTALLING_TITLE "ئۆچۈرۈش"
  ${LangFileString} MUI_UNTEXT_UNINSTALLING_SUBTITLE " $(^NameDA)نى ئۆچۈرۈۋاتىدۇ... تەخىر قىلىڭ  ."
  ${LangFileString} MUI_UNTEXT_FINISH_TITLE "ئۆچۈرۈش تاماملاندى"
  ${LangFileString} MUI_UNTEXT_FINISH_SUBTITLE "مۇۋەپپىقىيەتلىك ئۆچۈرۈلدى."
  ${LangFileString} MUI_UNTEXT_ABORT_TITLE "ئۆچۈرەلمىدى"
  ${LangFileString} MUI_UNTEXT_ABORT_SUBTITLE "ئۆچۈرۈش مەغلۇپ بولدى."
!endif

!ifdef MUI_FINISHPAGE
  ${LangFileString} MUI_TEXT_FINISH_INFO_TITLE "قاچىلاش يېتەكچىسى$(^NameDA)نى قاچىلاشنى تاماملىدى  "
  ${LangFileString} MUI_TEXT_FINISH_INFO_TEXT "$(^NameDA)كومپيۇتېرىڭىزغا قاچىلاندى .$\r$\n$\r$\nتامام نى بېسىپ قاچىلاش يېتەكچىسىنى يېپىڭ."
  ${LangFileString} MUI_TEXT_FINISH_INFO_REBOOT " $(^NameDA) نى قاچىلاشنى تاماملاش ئۈچۈن،كومپيۇتېرىڭىزنى قايتا قوزغىتىڭ كومپيۇتېرىڭىزنى قايتا قوزغىتامسىز؟ ."
!endif

!ifdef MUI_UNFINISHPAGE
  ${LangFileString} MUI_UNTEXT_FINISH_INFO_TITLE "ئۆچۈرۈش يېتەكچىسى$(^NameDA)نى ئۆچۈرۈشنى تاماملىدى  "
  ${LangFileString} MUI_UNTEXT_FINISH_INFO_TEXT "$(^NameDA)كومپيۇتېرىڭىزدىن ئۆچۈرۈلدى .$\r$\n$\r$\nئۆچۈرۈش يېتەكچىسىنى يېپىش ئۈچۈن تامام نى بېسىڭ."
  ${LangFileString} MUI_UNTEXT_FINISH_INFO_REBOOT " $(^NameDA) نى ئۆچۈرۈشنى تاماملاش ئۈچۈن،كومپيۇتېرىڭىزنى قايتا قوزغىتىڭ كومپيۇتېرىڭىزنى قايتا قوزغىتامسىز؟ "
!endif

!ifdef MUI_FINISHPAGE | MUI_UNFINISHPAGE
  ${LangFileString} MUI_TEXT_FINISH_REBOOTNOW "ھازىرلا قايتا قوزغۇتۇش"
  ${LangFileString} MUI_TEXT_FINISH_REBOOTLATER "كېيىنرەك كومپيۇتېرنى قولدا قايتا قوزغىتاي"
  ${LangFileString} MUI_TEXT_FINISH_RUN "&$(^NameDA)نى ئىجرا قىلىش "
  ${LangFileString} MUI_TEXT_FINISH_SHOWREADME "&ياردەمنى كۆرۈش"
  ${LangFileString} MUI_BUTTONTEXT_FINISH "&تامام"  
!endif

!ifdef MUI_STARTMENUPAGE
  ${LangFileString} MUI_TEXT_STARTMENU_TITLE "باشلاش تىزىملىكى قىسقۇچى تاللاش"
  ${LangFileString} MUI_TEXT_STARTMENU_SUBTITLE "$(^NameDA)تېزكۆرگۈچى ئۈچۈن باشلاش تىزىملىكى قىسقۇچى تاللاش  ."
  ${LangFileString} MUI_INNERTEXT_STARTMENU_TOP "پروگراممىنىڭ تېزكۆرگۈچىنى ياسايدىغان باشلاش تىزىملىك قىسقۇچىنى تاللاڭ ھەم يېڭى قىسقۇچقا ئىسىم كىرگۈزۈڭ."
  ${LangFileString} MUI_INNERTEXT_STARTMENU_CHECKBOX "تېزكۆرگۈچ ياسىمىسۇن"
!endif

!ifdef MUI_UNCONFIRMPAGE
  ${LangFileString} MUI_UNTEXT_CONFIRM_TITLE "$(^NameDA)نى ئۆچۈرۈش "
  ${LangFileString} MUI_UNTEXT_CONFIRM_SUBTITLE "$(^NameDA)نى كومپيۇتېردىن ئۆچۈرۈش ."
!endif

!ifdef MUI_ABORTWARNING
  ${LangFileString} MUI_TEXT_ABORTWARNING " $(^NameDA)نى قاچىلاشتىن چېكىنەمسىز؟"
!endif

!ifdef MUI_UNABORTWARNING
  ${LangFileString} MUI_UNTEXT_ABORTWARNING " $(^NameDA)نى ئۆچۈرۈشتىن چېكىنەمسىز؟"
!endif

!ifdef MULTIUSER_INSTALLMODEPAGE
  ${LangFileString} MULTIUSER_TEXT_INSTALLMODE_TITLE "ئىشلەتكۈچى تاللاش"
  ${LangFileString} MULTIUSER_TEXT_INSTALLMODE_SUBTITLE " $(^NameDA) نى قاچىلاش ئۈچۈن ئىشلەتكۈچىلەرنى تاللاڭ."
  ${LangFileString} MULTIUSER_INNERTEXT_INSTALLMODE_TOP " $(^NameDA) نى ئۆزى ئۈچۈنلا قاچىلاش ياكى بارلىق  ئىشلەتكۈچىلەر ئۈچۈن قاچىلاشنى تاللاش. $(^ClickNext)"
  ${LangFileString} MULTIUSER_INNERTEXT_INSTALLMODE_ALLUSERS "بارلىق ئىشلەتكۈچىلەرگە قاچىلاش"
  ${LangFileString} MULTIUSER_INNERTEXT_INSTALLMODE_CURRENTUSER "ماڭىلا قاچىلاش"
!endif
