object Form1: TForm1
  Left = 371
  Top = 343
  Width = 758
  Height = 487
  Caption = #26152#26085#37325#29616#20027#30028#38754
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  OnCreate = FormCreate
  OnDestroy = FormDestroy
  PixelsPerInch = 96
  TextHeight = 13
  object stat: TStatusBar
    Left = 0
    Top = 430
    Width = 742
    Height = 19
    Panels = <
      item
        Width = 150
      end
      item
        Width = 50
      end>
  end
  object btn: TButton
    Left = 8
    Top = 8
    Width = 75
    Height = 25
    Caption = #24320#22987#25195#25551
    TabOrder = 1
    OnClick = btnClick
  end
  object lv: TAdvListView
    Left = 8
    Top = 40
    Width = 729
    Height = 385
    Columns = <
      item
        Caption = #32929#31080#20195#30721
        Width = 80
      end
      item
        Caption = #32929#31080#21517#31216
        Width = 100
      end
      item
        Caption = #26368#20302#20215
        Width = 70
      end
      item
        Caption = #26368#39640#20215
        Width = 70
      end
      item
        Caption = #38454#27573#28072#24133
        Width = 100
      end
      item
        Caption = #19971#23544#20215
        Width = 80
      end
      item
        Caption = #26368#20302#20215
        Width = 80
      end
      item
        Caption = #24403#21069#20215
        Width = 80
      end
      item
        Caption = #29366#24577
      end>
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -16
    Font.Name = 'MS Sans Serif'
    Font.Style = []
    HotTrack = True
    HoverTime = -1
    ReadOnly = True
    RowSelect = True
    ParentFont = False
    TabOrder = 2
    ViewStyle = vsReport
    AutoHint = False
    ClipboardEnable = False
    ColumnSize.Save = False
    ColumnSize.Stretch = False
    ColumnSize.Storage = stInifile
    FilterTimeOut = 500
    PrintSettings.FooterSize = 0
    PrintSettings.HeaderSize = 0
    PrintSettings.Time = ppNone
    PrintSettings.Date = ppNone
    PrintSettings.DateFormat = 'dd/mm/yyyy'
    PrintSettings.PageNr = ppNone
    PrintSettings.Title = ppNone
    PrintSettings.Font.Charset = DEFAULT_CHARSET
    PrintSettings.Font.Color = clWindowText
    PrintSettings.Font.Height = -11
    PrintSettings.Font.Name = 'MS Sans Serif'
    PrintSettings.Font.Style = []
    PrintSettings.HeaderFont.Charset = DEFAULT_CHARSET
    PrintSettings.HeaderFont.Color = clWindowText
    PrintSettings.HeaderFont.Height = -11
    PrintSettings.HeaderFont.Name = 'MS Sans Serif'
    PrintSettings.HeaderFont.Style = []
    PrintSettings.FooterFont.Charset = DEFAULT_CHARSET
    PrintSettings.FooterFont.Color = clWindowText
    PrintSettings.FooterFont.Height = -11
    PrintSettings.FooterFont.Name = 'MS Sans Serif'
    PrintSettings.FooterFont.Style = []
    PrintSettings.Borders = pbNoborder
    PrintSettings.BorderStyle = psSolid
    PrintSettings.Centered = False
    PrintSettings.RepeatHeaders = False
    PrintSettings.LeftSize = 0
    PrintSettings.RightSize = 0
    PrintSettings.ColumnSpacing = 0
    PrintSettings.RowSpacing = 0
    PrintSettings.Orientation = poPortrait
    PrintSettings.FixedWidth = 0
    PrintSettings.FixedHeight = 0
    PrintSettings.UseFixedHeight = False
    PrintSettings.UseFixedWidth = False
    PrintSettings.FitToPage = fpNever
    PrintSettings.PageNumSep = '/'
    HTMLHint = False
    HTMLSettings.Width = 100
    HeaderHotTrack = False
    HeaderDragDrop = False
    HeaderFlatStyle = False
    HeaderOwnerDraw = False
    HeaderHeight = 13
    HeaderFont.Charset = DEFAULT_CHARSET
    HeaderFont.Color = clWindowText
    HeaderFont.Height = -16
    HeaderFont.Name = 'MS Sans Serif'
    HeaderFont.Style = []
    ProgressSettings.ColorFrom = clSilver
    ProgressSettings.FontColorFrom = clBlack
    ProgressSettings.ColorTo = clWhite
    ProgressSettings.FontColorTo = clGray
    SelectionRTFKeep = False
    ScrollHint = False
    SelectionColor = clHighlight
    SelectionTextColor = clHighlightText
    SizeWithForm = False
    SortDirection = sdDescending
    SortShow = True
    SortIndicator = siLeft
    StretchColumn = False
    SubImages = False
    SubItemEdit = False
    SubItemSelect = False
    VAlignment = vtaCenter
    ItemHeight = 13
    SaveHeader = False
    LoadHeader = False
    ReArrangeItems = False
    DetailView.Visible = False
    DetailView.Column = 0
    DetailView.Font.Charset = DEFAULT_CHARSET
    DetailView.Font.Color = clBlue
    DetailView.Font.Height = -11
    DetailView.Font.Name = 'MS Sans Serif'
    DetailView.Font.Style = []
    DetailView.Height = 16
    DetailView.Indent = 0
    DetailView.SplitLine = False
    Version = '1.6.4.9'
  end
  object trayIcon: TCoolTrayIcon
    CycleInterval = 0
    Hint = #19971#23544#25112#27861
    Icon.Data = {
      0000010001002020040000000000E80200001600000028000000200000004000
      0000010004000000000000020000000000000000000000000000000000000000
      000000008000008000000080800080000000800080008080000080808000C0C0
      C0000000FF0000FF000000FFFF00FF000000FF00FF00FFFF0000FFFFFF000000
      0000000000000000000000000000000000000000000000000000000000000000
      0000000000000000000000000000000000000000000000000000000000007777
      77777777777777777777777777007FFFFFFFFFFFFFFFFFFFFFFFFFFFF7007FFF
      FFFFFFFFFFFFFFFFFFFFFFFFF7007FFFFFFFFFFFFFFFFFFFFFFFFFFFF7007FF8
      88888888888FF888888FFFFFF7007FF877777777778FFFFFFFFFFFFFF7007FF8
      77777777778FF888888FFFFFF7007FF877777777778FFFFFFFFFFFFFF7007FF8
      77777777778FFFFFFFFFFFFFF7007FF877777777778FF888888FFFFFF7007FF8
      77777777778FFFFFFFFFFFFFF7007FF877777777778FF888888FF77FF7007FF8
      77777777778FFFFFFFFFF77FF7007FF877777777778FF888888FF77FF7007FF8
      87777777778FFFFFFFFFF77FF7007FF877777777778FF888888FF77FF7007FF8
      77777777778FFFFFFFFFF67FF7007FF877777777778FF888888FF66FF7007FF8
      77777777778FFFFFFFFFF66FF7007FF888888888888FF888888FF66FF7007FFF
      FFFFFFFFFFFFFFFFFFFFFFFFF7007FFFFFFFFFFFFFFFFFFFFFFFFFFFF7007888
      8888888888888888888888888700788888888888888888888887878787007777
      7777777777777777777777777700000000000000000000000000000000000000
      000000000000000000000000000000000000000000000000000000000000FFFF
      FFFFFFFFFFFF0000000000000000000000000000000000000000000000000000
      0000000000000000000000000000000000000000000000000000000000000000
      0000000000000000000000000000000000000000000000000000000000000000
      000000000000000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF}
    IconIndex = 0
    PopupMenu = pm
    MinimizeToTray = True
    Left = 424
    Top = 8
  end
  object pm: TPopupMenu
    Left = 456
    Top = 8
    object N1: TMenuItem
      Caption = #26174#31034#20027#30028#38754
      OnClick = N1Click
    end
    object N2: TMenuItem
      Caption = #25552#31034#20449#24687
    end
  end
  object msnp: TMSNPopUp
    Text = 'text'
    Width = 400
    Height = 200
    GradientColor1 = 16764057
    GradientColor2 = clWhite
    ScrollSpeed = 9
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -11
    Font.Name = 'MS Sans Serif'
    Font.Style = []
    HoverFont.Charset = DEFAULT_CHARSET
    HoverFont.Color = clBlue
    HoverFont.Height = -11
    HoverFont.Name = 'MS Sans Serif'
    HoverFont.Style = [fsUnderline]
    Title = 'title'
    TitleFont.Charset = DEFAULT_CHARSET
    TitleFont.Color = clWindowText
    TitleFont.Height = -11
    TitleFont.Name = 'MS Sans Serif'
    TitleFont.Style = [fsBold]
    Options = [msnCascadePopups, msnAllowScroll]
    TextAlignment = taCenter
    TextCursor = crDefault
    PopupMarge = 2
    PopupStartX = 16
    PopupStartY = 2
    DefaultMonitor = dmDesktop
    OnClick = msnpClick
    Left = 488
    Top = 8
  end
  object con: TADOConnection
    ConnectionString = 'Provider=MSDASQL.1;Persist Security Info=False;Data Source=stock'
    LoginPrompt = False
    Provider = 'MSDASQL.1'
    Left = 520
    Top = 8
  end
  object ds: TADODataSet
    Connection = con
    Parameters = <>
    Left = 544
    Top = 8
  end
  object httpget: THTTPGet
    AcceptTypes = '*/*'
    Agent = 'UtilMind HTTPGet'
    BinaryData = False
    UseCache = False
    WaitThread = False
    OnDoneString = httpgetDoneString
    OnError = httpgetError
    Left = 584
    Top = 8
  end
  object tmr: TTimer
    Enabled = False
    OnTimer = tmrTimer
    Left = 392
    Top = 8
  end
  object query: TADOQuery
    Connection = con
    Parameters = <>
    Left = 360
    Top = 8
  end
end
