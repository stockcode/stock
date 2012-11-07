object frmMain: TfrmMain
  Left = 522
  Top = 358
  Width = 388
  Height = 551
  Caption = #33258#21160#20132#26131#31995#32479
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  OnCreate = FormCreate
  PixelsPerInch = 96
  TextHeight = 13
  object Memo1: TMemo
    Left = 16
    Top = 88
    Width = 345
    Height = 417
    TabOrder = 0
  end
  object btnHistory: TButton
    Left = 96
    Top = 48
    Width = 129
    Height = 25
    Caption = #36873#25321#21382#21490#20132#26131#25991#20214
    TabOrder = 1
    OnClick = btnHistoryClick
  end
  object btnStop: TButton
    Left = 112
    Top = 16
    Width = 113
    Height = 25
    Caption = #20572#27490#33258#21160#36816#34892
    TabOrder = 2
    OnClick = btnStopClick
  end
  object btnAccount: TButton
    Left = 232
    Top = 48
    Width = 129
    Height = 25
    Caption = #36873#25321#36164#37329#32929#20221#25991#20214
    TabOrder = 3
    OnClick = btnAccountClick
  end
  object dt: TDateTimePicker
    Left = 272
    Top = 16
    Width = 90
    Height = 21
    Date = 40690.668876678240000000
    Time = 40690.668876678240000000
    TabOrder = 4
  end
  object ADOConnection1: TADOConnection
    ConnectionString = 'Provider=MSDASQL.1;Persist Security Info=False;Data Source=stock'
    LoginPrompt = False
    Provider = 'MSDASQL.1'
    Left = 80
    Top = 16
  end
  object ds: TADODataSet
    Connection = ADOConnection1
    Parameters = <>
    Left = 48
    Top = 16
  end
  object IdSMTP: TIdSMTP
    MaxLineAction = maException
    ReadTimeout = 0
    Host = 'smtp.139.com'
    Port = 25
    AuthenticationType = atLogin
    Password = 'gk790624'
    Username = '13613803575'
    Left = 16
    Top = 48
  end
  object IdMessage: TIdMessage
    AttachmentEncoding = 'MIME'
    BccList = <>
    CCList = <>
    Encoding = meMIME
    From.Address = '13613803575@139.com'
    From.Text = '13613803575@139.com'
    Recipients = <
      item
        Address = '13613803575@139.com'
        Text = '13613803575@139.com'
      end>
    ReplyTo = <
      item
        Address = '13613803575@139.com'
        Text = '13613803575@139.com'
      end>
    Left = 56
    Top = 48
  end
  object tmrOperate: TTimer
    Enabled = False
    Interval = 3000
    OnTimer = tmrOperateTimer
    Left = 16
    Top = 16
  end
  object tmrSearch: TTimer
    Enabled = False
    Interval = 3000
    OnTimer = tmrSearchTimer
    Left = 152
    Top = 48
  end
  object ADOQuery: TADOQuery
    Connection = ADOConnection1
    Parameters = <>
    Left = 128
    Top = 48
  end
  object dlgOpen: TOpenDialog
    Left = 96
    Top = 48
  end
end
