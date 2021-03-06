unit u_main;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, ShellApi, DB, ADODB,strutils, IdBaseComponent,
  IdComponent, IdTCPConnection, IdTCPClient, IdMessageClient, IdSMTP,
  IdMessage, ExtCtrls, clipbrd, ComCtrls;

type
  TfrmMain = class(TForm)
    ADOConnection1: TADOConnection;
    ds: TADODataSet;
    IdSMTP: TIdSMTP;
    IdMessage: TIdMessage;
    tmrOperate: TTimer;
    Memo1: TMemo;
    ADOQuery: TADOQuery;
    btnHistory: TButton;
    dlgOpen: TOpenDialog;
    btnStop: TButton;
    btnAccount: TButton;
    dt: TDateTimePicker;
    ds1: TADODataSet;
    procedure SendMail(subject:String; body:String);
    procedure CalculateTrade(parent:HWND);
    procedure CalculateDelegation(parent:HWND);
    procedure CalculateStockAccount(parent:HWND);
    procedure CalculateMoneyAccount(parent:HWND);
    procedure CalculateGXMoneyAccount(); //国信证券
    procedure CalculateGXStockAccount(); //国信证券
    procedure CalculateGXTrade(); //国信证券
    function Operate(operation:String;StockCode:String;price:String;amount:String):String;
    function  GetDialog(Caption:String):String;
    function  GetGDDialog(Caption:String):String; //光大证券
    procedure CancelGDDelegation(); //光大证券
    procedure CalGDTrade(); //光大证券
    function GetDialogHWND(parent:HWND;Caption:String):HWND;
    function GetWindowHWND(Caption:String):HWND;
    function GetVisibleDialogHWND(parent:HWND):HWND;    
    function GetText(h:HWND):String;
    procedure tmrOperateTimer(Sender: TObject);
    procedure StartWeiTuo();
    procedure tmrSearchTimer(Sender: TObject);
    procedure btnHistoryClick(Sender: TObject);
    procedure btnStopClick(Sender: TObject);
    procedure btnAccountClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmMain: TfrmMain;
  mnuWnd, tdx, tdxWnd:HWND;

implementation

{$R *.dfm}


function TfrmMain.Operate(operation:String; StockCode: String; price: String;
  amount: String):String;
var
  h,h1,h2,h3,yesBtn, noBtn, child,panel,okBtn :hwnd;
  Caption: PChar;
  stockamount, stockprice:String;
  CaptionLength :Integer;
  sl:TStringList;
  Ret: LongInt;
begin
  postmessage(mnuWnd, WM_LBUTTONDOWN, 0, MakeLParam(200,10));
  postmessage(mnuWnd, WM_LBUTTONUP, 0, MakeLParam(200,10));

  if operation = 'BUY' then begin
      postmessage(mnuWnd, WM_LBUTTONDOWN, 0, MakeLParam(20,10));
      postmessage(mnuWnd, WM_LBUTTONUP, 0, MakeLParam(20,10));
      h1:= FindWindowEx(tdxWnd, 0, 'TFrmBuyStock1', nil);
  end
  else begin
      postmessage(mnuWnd, WM_LBUTTONDOWN, 0, MakeLParam(80,10));
      postmessage(mnuWnd, WM_LBUTTONUP, 0, MakeLParam(80,10));
      h1:= FindWindowEx(tdxWnd, 0, 'TFrmSellStock1', nil);
  end;
  sleep(1000);
  

  h1 := GetWindow(h1,GW_CHILD); //TPanel
  panel := GetWindow(h1,GW_HWNDNEXT); //TPanel
  okBtn := FindWindowEx(Panel,0,'TButton', '委托[F3]'); //下单

  if operation = 'BUY' then
    h3 := GetWindow(okBtn,GW_HWNDNEXT) //TEdit
  else
    h3 := GetWindow(okBtn,GW_HWNDPREV); //TEdit

  sendmessage(h3,WM_SETTEXT,0,Integer(PChar(StockCode)));
  postmessage(h3,WM_KEYDOWN, VK_RETURN,0); //按下回车
  sleep(1000);

  h1 := FindWindowEx(panel,0,'TStockComboBox', nil);
  h3 := GetWindow(h1,GW_CHILD); //TEdit 价格

  sendmessage(h3,WM_SETTEXT,0,Integer(PChar(price)));
  postmessage(h3,WM_KEYDOWN,VK_RETURN,0); //按下回车
  sleep(2000);

  h3 := GetWindow(h1,GW_HWNDNEXT);
  h3 := GetWindow(h3,GW_HWNDNEXT);

  if operation = 'SELL' then begin
    stockamount := GetText(h3);
    if amount = 'all' then amount := stockamount
    else amount := IntToStr(strtoint(stockamount) div 2);
  end;
  
  Memo1.Lines.Add(amount);

  h3:=FindWindowEx(panel,0,'THSNumComBoBox', nil); //卖出数量
  sendmessage(h3,CB_ADDSTRING,0,Integer(PChar(amount)));
  Ret := SendMessage(h3, CB_FINDSTRINGEXACT, -1, LongInt(PChar(amount)));
 if Ret = -1 then begin
   ShowMessage('没有找到品种2!');
   Exit;
 end;
 SendMessage(h3,CB_SETCURSEL,Ret,0);


  sleep(3000);

  postmessage(okBtn,WM_LBUTTONDOWN,0,0); //按下鼠标
  postmessage(okBtn,WM_LBUTTONUP,0,0);  //释放鼠标

  sleep(2000);

   h := FindWindowEx(0,0,'TfrmDialogs', '确认');
   h := GetWindow(h,GW_CHILD); //TPanel
   h := GetWindow(h,GW_HWNDNEXT); //TPanel
   h := GetWindow(h,GW_CHILD); //TGridPanel

      yesBtn := FindWindowEx(h,0,'Tbutton', '是(&Y)');
        postmessage(yesBtn,WM_LBUTTONDOWN,0,0); //按下鼠标
        postmessage(yesBtn,WM_LBUTTONUP,0,0);  //释放鼠标

        sleep(3000);
        Result := GetGDDialog('金阳光提醒您');
        if Result = '' then begin
          Result := GetDialog('提交失败');
        end;


end;

function TfrmMain.GetDialog(Caption: String): String;
var
  h,h1,child,yesBtn, closeBtn:HWND;
  pCaption: PChar;
  CaptionLength :Integer;
begin
  Result := '';
   h := FindWindowEx(0,0,'#32770', nil);
   while h <> 0 do begin
    child := GetWindow(h, GW_CHILD);
    while (child <> 0) do begin
      CaptionLength := SendMessage(child, WM_GETTEXTLENGTH, 0, 0) + 1;
      getmem(pCaption,CaptionLength);
      sendmessage(child,WM_GETTEXT,CaptionLength,integer(pCaption));

      if pCaption = '确认' then begin
        yesBtn := child;
      end;

      if pCaption = '退出系统' then closeBtn := child;

      if AnsiStartsText(Caption, pCaption) then begin
        result := pCaption;
        postmessage(yesBtn,WM_LBUTTONDOWN,0,0);
        postmessage(yesBtn,WM_LBUTTONUP,0,0);

        postmessage(closeBtn,WM_LBUTTONDOWN,0,0);
        postmessage(closeBtn,WM_LBUTTONUP,0,0);

        exit;
      end;
      child := GetWindow(child, GW_HWNDNEXT);
    end;
    h := GetWindow(h, GW_HWNDNEXT);
   end;
end;

procedure TfrmMain.SendMail(subject, body: String);
begin
  idMessage.Subject := subject;
  idmessage.Body.Add(body);

  idSmtp.Connect();
  idsmtp.Authenticate;
  idSMTP.Send(idMessage);
end;

procedure TfrmMain.tmrOperateTimer(Sender: TObject);
var
  h,h1,h2,h3,h4,e,e1,e2:hwnd;
  operdate, status, stockcode, stockamount, stockprice,operation:String;
  money :Integer;
  price:Double;
  results :TStringList;
  n:Integer;
  sl,sline:TStringList;
  i, btnPosition:Integer;
  famount, stockName, fileName, line,tradedate:String;
  vi:TOSVersionInfoA;
begin


  tmrOperate.Enabled := false;




  tdx:=FindWindowEx(0, 0, 'TfrmForexMainGUIHSINFO', '光大证券网上交易');

  setforegroundwindow(tdx);

  h1:=FindWindowEx(tdx,0,'TfrmBizContnr', nil);
  mnuWnd := GetWindow(h1,GW_HWNDNEXT);
  mnuWnd := GetWindow(mnuWnd,GW_CHILD);
  mnuWnd := GetWindow(mnuWnd,GW_CHILD);

  h1 := FindWindowEx(h1,0, nil, 'pnlBottom'); //TPanel
  h1 := GetWindow(h1,GW_CHILD); //Afx
  tdxWnd := GetWindow(h1,GW_HWNDNEXT); //AfxMDIFrame42

  CalGDTrade;

  CancelGDDelegation;

    ds.Active := false;
    ds.CommandText := 'select tradedate from stockaccount where loginid=''sunsulian'' order by tradedate desc limit 0,1';
    ds.Active := true;

  tradedate := DateToStr(Now);
  if not ds.Eof then tradedate := ds.fieldbyname('tradedate').AsString;

  ds.Active := False;
  ds.CommandText := 'select * from stockaccount where isauto=1 and loginid=''sunsulian'' and tradedate=' + QuotedStr(tradedate);
  ds.Active := true;


  while not ds.Eof do begin
    stockCode := ds.fieldbyname('stockcode').AsString;
    stockName := ds.fieldbyname('stockname').AsString;
    operation := ds.fieldbyname('oper').AsString;
    operdate := ds.fieldbyname('operdate').AsString;

    money := ds.fieldbyname('amount').AsInteger + ds.fieldbyname('remainamount').AsInteger;
    ds1.Active := false;
    ds1.CommandText := 'select ub,lb from mn5 where stockcode=' + QuotedStr(stockcode) + ' order by tradedate desc limit 0,1';
    ds1.Active := true;

    stockprice := ds1.fieldbyname('ub').AsString;

    if operation = 'BUY' then  begin
       stockprice := ds1.fieldbyname('lb').AsString;
       money := (Round(money / StrToFloat(stockprice)) div 100) * 100;
       stockamount := IntToStr(money);
    end else if operdate = DateToStr(Now) then begin
      stockamount := 'all';
    end;

    ds1.Active := false;

    status := Operate(operation, stockcode, stockprice, stockamount);
    ds.Next;
    stockamount := '';
  end;


//    ds.FieldByName('result').Value := status;
//    ds.Post;
//    if operation = 'BUY' then operation := '买入'
//    else operation := '卖出';
//
//    results.Add(IntToStr(n) + '.股票代码：' + stockcode + ', '+operation+'金额：' + stockprice + ', '+operation+'数量：' + stockamount + ',委托结果：' + status);
//    inc(n);
//    sleep(1000);
//    ds.Next;
//  end;
//  ds.Close;

//  sendmail('股票: 委托完毕', results.GetText);
  Close;
end;

function TfrmMain.GetDialogHWND(parent:HWND;Caption: String): HWND;
var
  h,h1,child :HWND;
  pCaption: PChar;
  CaptionLength :Integer;
begin
   h := FindWindowEx(parent,0,'#32770', nil);
   while h <> 0 do begin
    child := GetWindow(h, GW_CHILD);
    while (child <> 0) do begin
      CaptionLength := SendMessage(child, WM_GETTEXTLENGTH, 0, 0) + 1;
      getmem(pCaption,CaptionLength);
      sendmessage(child,WM_GETTEXT,CaptionLength,integer(pCaption));

      Memo1.Lines.Add(pCaption);
      
      if AnsiStartsText(Caption, pCaption) then begin
        result := h;
        exit;
      end;
      child := GetWindow(child, GW_HWNDNEXT);
      FreeMem(pCaption);
    end;
    h := GetWindow(h, GW_HWNDNEXT);
   end;
end;

procedure TfrmMain.StartWeiTuo;
var
  h,h1,h2,h4:HWND;
  n,retry:Integer;
  xiadanPath:Pchar;
begin
  retry := 0;
  xiadanPath := 'C:\new_gxzq_v6\WinWT.exe';
  h:=GetWindowHWND('通达信网上交易');

  if (h = 0) then begin
    ShellExecute(Handle, 'open', xiadanPath, nil, nil, SW_SHOWNORMAL) ;
    sleep(3000);
    h:=GetWindowHWND('通达信网上交易');
    h1:=FindWindowEx(h,0,'AfxWnd42', nil);
    sendmessage(h1,WM_SETTEXT,0,Integer(PChar('858585')));
    h1:=FindWindowEx(h,0,nil, '保护帐号');
    h1 := GetWindow(h1, GW_HWNDNEXT);
    sendmessage(h1,WM_SETTEXT,0,Integer(PChar('lp850209')));
    h4:=FindWindowEx(h,0, 'button', '确定');
    postmessage(h4,WM_LBUTTONDOWN,0,0); //按下鼠标
    postmessage(h4,WM_LBUTTONUP,0,0);  //释放鼠标
    Sleep(5000);
    GetDialog('今日不再提示');
  end;

  h:=GetWindowHWND('通达信网上交易');
  setforegroundwindow(h);

  while GetWindowHWND('通达信网上交易') = 0 do
  begin
    sleep(10000);
    if (retry > 3) then
    begin
      SendMail('股票: 下单程序启动失败，无法完成交易', '下单程序地址：' + xiadanPath);
      Close;
    end;
    inc(retry);
  end;
  Sleep(3000);
end;

procedure TfrmMain.tmrSearchTimer(Sender: TObject);
var
  h,h1,h2,h3:HWND;
  zjye,djje,kyje,kqje,gpsz,zzc, tradedate,mailbody:String;
  pt:TPoint;
  vi:TOSVersionInfoA;
  btnPosition:Integer;
begin
  btnPosition := 150;
  vi.dwOSVersionInfoSize := SizeOf(TOSVersionInfo);
  if GetVersionEx(VI) then begin
    if vi.dwMajorVersion = 5 then begin
      btnPosition := 170;
    end;
  end;



  StartWeiTuo();

  tdx:=GetWindowHWND('通达信网上交易');

  h1:=FindWindowEx(tdx,0,'AfxFrameOrView42', nil);
  h1 := GetWindow(h1,GW_CHILD); //Dialog
  h1 := GetWindow(h1,GW_CHILD); //Afx
  h1 := GetWindow(h1,GW_CHILD); //AfxMDIFrame42
  h1 := GetWindow(h1,GW_CHILD); //AfxWnd42
  tdxWnd := GetWindow(h1,GW_HWNDNEXT); //AfxWnd42

  h2 := GetWindow(tdxWnd,GW_CHILD); //MHPDockBar
  h2 := GetWindow(h2,GW_CHILD); //MHPToolBar
  postmessage(h2, WM_LBUTTONDOWN, 0, MakeLParam(btnPosition,10));
  postmessage(h2, WM_LBUTTONUP, 0, MakeLParam(btnPosition,10));
  sleep(3000);

  CalculateGXMoneyAccount(); //查询资金
  sleep(1000);
  CalculateGXStockAccount(); //查询股票
  sleep(3000);
  CalculateGXTrade(); // 当日成交
  sleep(1000);

  postmessage(tdx,WM_CLOSE,0,0);
  Sleep(1000);
  GetDialog('谢谢使用');
  Close;
end;

function TfrmMain.GetText(h: HWND): String;
var
  Caption: PChar;
  CaptionLength :Integer;
begin
  CaptionLength := SendMessage(h, WM_GETTEXTLENGTH, 0, 0) + 1;
  getmem(Caption,CaptionLength);
  sendmessage(h,WM_GETTEXT,CaptionLength,integer(Caption));
  result := Caption;
end;

procedure TfrmMain.CalculateDelegation(parent: HWND);
var
  h2:HWND;
  sl,sline:TStringList;
  i:Integer;
  line,tradedate:String;
begin
  sleep(3000);
  tradedate := DateToStr(Now);
  h2:=GetDialogHWND(parent, '显示分笔成交'); //当日成交
  h2:=GetWindow(h2,GW_HWNDNEXT); //当日委托  
  h2 := FindWIndowEx(h2, 0, nil, 'HexinScrollWnd');
  h2 := FindWIndowEx(h2, 0, nil, 'HexinScrollWnd2');
  h2 := FindWIndowEx(h2, 0, 'CVirtualGridCtrl', nil);
  postmessage(h2, WM_RBUTTONDOWN, 0, 0);
  postmessage(h2, WM_RBUTTONUP, 0, 0);
  sleep(500);
  postmessage(h2,WM_KEYDOWN,67,0); //复制
  Sleep(1000);
  sline := TStringList.Create;
  sl := TStringList.Create;
  line := StringReplace(Clipboard.AsText, #13, ',', [rfReplaceAll]);
  line := StringReplace(line, #9, '#', [rfReplaceAll]);
  sl.CommaText := line;
  for i:=1 to sl.Count - 1 do begin
    sline.Delimiter := '#';
    sline.DelimitedText := sl[i];
    ADOQuery.SQL.Clear;
    ADOQuery.SQL.Add(Format('insert into delegatelog(delegatetime,stockcode, stockname, operation, memo, delegateamount, ordertype, tradeamount, delegateprice, tradeprice, contractno, tradedate) values(''%s'',''%s'',''%s'',''%s'',''%s'',%s,''%s'',%s,%s,%s,%s,''%s'')', [tradedate + ' ' + sline[0],sline[1],sline[2],sline[3],sline[4],sline[5],sline[6],sline[7],sline[8],sline[9],sline[10],tradedate]));
    ADOQuery.ExecSQL;
  end;
  sl.Free;
  sline.Free;
end;

procedure TfrmMain.CalculateStockAccount(parent: HWND);
var
  h2:HWND;
  sl,sline:TStringList;
  i:Integer;
  line,tradedate:String;
begin
  tradedate := DateToStr(Now);
  h2:=GetDialogHWND(parent, '查询资金股票');
  h2 := FindWIndowEx(h2, 0, nil, 'HexinScrollWnd');
  h2 := FindWIndowEx(h2, 0, nil, 'HexinScrollWnd2');
  h2 := FindWIndowEx(h2, 0, 'CVirtualGridCtrl', nil);
  postmessage(h2, WM_RBUTTONDOWN, 0, 0);
  postmessage(h2, WM_RBUTTONUP, 0, 0);
  sleep(500);
  postmessage(h2,WM_KEYDOWN,67,0); //复制
  Sleep(1000);
  sline := TStringList.Create;
  sl := TStringList.Create;
  line := StringReplace(Clipboard.AsText, #13, ',', [rfReplaceAll]);
  line := StringReplace(line, #9, '#', [rfReplaceAll]);
  sl.CommaText := line;

  for i:=1 to sl.Count - 1 do begin
    sline.Delimiter := '#';
    sline.DelimitedText := sl[i];
    ADOQuery.SQL.Clear;
    ADOQuery.SQL.Add(Format('insert into stockaccount(stockcode, stockname, stockremain, stockusable, profit, costprice, plpercent, currentprice, currentmoney, tradedate) values(''%s'',''%s'',%s,%s,%s,%s,%s,%s,%s,''%s'')', [sline[0],sline[1],sline[2],sline[3],sline[4],sline[5],sline[6],sline[7],sline[8],tradedate]));
    ADOQuery.ExecSQL;
  end;
  sl.Free;
  sline.Free;
end;

procedure TfrmMain.CalculateMoneyAccount(parent: HWND);
var
  h2,h3:HWND;
  zjye,djje,kyje,kqje,gpsz,zzc, tradedate,mailbody:String;
begin
  h2:=GetDialogHWND(parent, '查询资金股票');
  h3 := FindWindowEx(h2,0,'static', nil);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  h3 := GetWindow(h3,GW_HWNDNEXT);

  zjye := GetText(h3);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  djje := GetText(h3);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  kyje := GetText(h3);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  kqje := GetText(h3);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  gpsz := GetText(h3);
  h3 := GetWindow(h3,GW_HWNDNEXT);
  zzc := GetText(h3);

  tradedate := DateToStr(Now);
  ADOQuery.SQL.Add(Format('insert into account(remain, frozen, available, usable, stock, total, tradedate) values(%s,%s,%s,%s,%s,%s,''%s'')', [zjye,djje,kyje,kqje,gpsz,zzc,tradedate]));
  adoquery.ExecSQL;
  mailbody := Format('%s日账户情况,资金余额:%s元,冻结金额:%s元,可用余额:%s元,可取金额:%s元,股票市值:%s元,总资产:%s元.', [tradedate, zjye,djje,kyje,kqje,gpsz,zzc]);
  sendmail('股票: 账户金额统计', mailbody);

end;

procedure TfrmMain.CalculateTrade(parent: HWND);
var
  h2:HWND;
  sl,sline:TStringList;
  i:Integer;
  line,tradedate:String;
begin
  sleep(3000);
  tradedate := DateToStr(Now);
  h2:=GetDialogHWND(parent, '显示分笔成交');
  h2 := FindWIndowEx(h2, 0, nil, 'HexinScrollWnd');
  h2 := FindWIndowEx(h2, 0, nil, 'HexinScrollWnd2');
  h2 := FindWIndowEx(h2, 0, 'CVirtualGridCtrl', nil);
  postmessage(h2, WM_RBUTTONDOWN, 0, 0);
  postmessage(h2, WM_RBUTTONUP, 0, 0);
  sleep(500);
  postmessage(h2,WM_KEYDOWN,67,0); //复制
  Sleep(1000);
  sline := TStringList.Create;
  sl := TStringList.Create;
  line := StringReplace(Clipboard.AsText, #13, ',', [rfReplaceAll]);
  line := StringReplace(line, #9, '#', [rfReplaceAll]);
  sl.CommaText := line;
  for i:=1 to sl.Count - 1 do begin
    sline.Delimiter := '#';
    sline.DelimitedText := sl[i];
    ADOQuery.SQL.Clear;
    ADOQuery.SQL.Add(Format('insert into tradelog(tradetime,stockcode, stockname, operation, tradeamount, tradeprice, trademoney, contractno, tradeno, tradedate) values(''%s'',''%s'',''%s'',''%s'',%s,%s,%s,%s,''%s'',''%s'')', [tradedate + ' ' + sline[0],sline[1],sline[2],sline[3],sline[4],sline[5],sline[6],sline[7],sline[8],tradedate]));
    ADOQuery.ExecSQL;
  end;
  sl.Free;
  sline.Free;
end;

procedure TfrmMain.CalculateGXMoneyAccount();
var
  sLine:TStringList;
  h1, h2,h3:HWND;
  remain,available,stock,total,profit,zzc, tradedate,mailbody:String;
begin

  h1:= GetVisibleDialogHWND(tdxWnd);


  h3 := FindWindowEx(h1,0, nil, '相关资讯');
  h3 := GetWindow(h3,GW_HWNDNEXT);

  sline := TStringList.Create;
  sline.Delimiter := ' ';
  sline.DelimitedText := GetText(h3);
  remain := sLine[0];
  Delete(remain, 1 , Pos(':', remain));
  available := sLine[1];
  Delete(available, 1 , Pos(':', available));
  stock := sLine[2];
  Delete(stock, 1 , Pos(':', stock));
  total := sLine[3];
  Delete(total, 1 , Pos(':', total));
  profit := sLine[4];
  Delete(profit, 1 , Pos(':', profit));

  remain :=FloatToStr(StrToFloat(total) - StrToFloat(stock));
  tradedate := DateToStr(Now);
  ADOQuery.SQL.Add(Format('insert into account(loginid, remain, stock, total, profit, tradedate) values(''gengke'',%s,%s,%s,%s,''%s'')', [remain,stock,total,profit,tradedate]));
  adoquery.ExecSQL;
  mailbody := Format('%s日账户情况,资金余额:%s元,可用金额:%s元,股票市值:%s元,总资产:%s元,盈亏:%s元.', [tradedate, remain,available,stock,total,profit]);
  sendmail('股票: 账户金额统计', mailbody);

end;

procedure TfrmMain.CalculateGXStockAccount();
var
  h1, h2:HWND;
  sl,sline:TStringList;
  i:Integer;
  famount, stockName, stockCode, fileName, line,tradedate:String;
begin
  tradedate := DateToStr(Now);

  h1:= GetVisibleDialogHWND(tdxWnd);

  h2 := FindWindowEx(h1,0, nil, '输 出');
  postmessage(h2, WM_LBUTTONDOWN, 0, 0);
  postmessage(h2, WM_LBUTTONUP, 0, 0);
  sleep(1000);
  h2 := FindWindowEx(0,0,'#32770', '输出');
  h1 := GetWindow(h2, GW_CHILD);
  postmessage(h1, WM_LBUTTONDOWN, 0, 0);
  postmessage(h1, WM_LBUTTONUP, 0, 0);

  h1 := GetWindow(h1, GW_HWNDNEXT);
  h1 := GetWindow(h1, GW_HWNDNEXT);
  fileName := GetText(h1);

  h1 := FindWindowEx(h2,0, nil, '确  定');
  postmessage(h1, WM_LBUTTONDOWN, 0, 0);
  postmessage(h1, WM_LBUTTONUP, 0, 0);
  Sleep(2000);



  sline := TStringList.Create;
  sl := TStringList.Create;
  sl.LoadFromFile(fileName);

  DeleteFile(fileName);

  fileName := ExtractFileName(fileName) + ' - 记事本';
  h1 := FindWindowEx(0, 0, 'Notepad', PChar(fileName));
  postmessage(h1, WM_CLOSE, 0, 0);


  for i:=4 to sl.Count - 1 do begin
    line := sl[i];
    stockCode := LeftStr(line, 6);
    Delete(line, 1, 16);
    stockName := AnsiLeftStr(line, 8);
    stockName := Trim(stockName);
    Delete(line, 1, 16);
    sline.Delimiter := ' ';
    sline.DelimitedText := line;

    if (sline[9] = 'A310556598') or (sline[9] = '0143924068') then famount := '0'
    else famount := sline[9];

    ADOQuery.SQL.Clear;
    ADOQuery.SQL.Add(Format('insert into stockaccount(loginid, stockcode, stockname, stockamount, sellable, buyedamount, costprice, buyprice, currentprice, currentmoney, profit, plpercent, frozenamount, tradedate) values(''gengke'', ''%s'',''%s'',%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,''%s'')', [stockCode,stockName,sline[0],sline[1],sline[2],sline[3],sline[4],sline[5],sline[6],sline[7],sline[8],famount,tradedate]));
    ADOQuery.ExecSQL;
  end;
  sl.Free;
  sline.Free;

end;

procedure TfrmMain.CalculateGXTrade();
var
  h1, h2:HWND;
  sl,sline:TStringList;
  i:Integer;
  fileName, line,tradedate, stockCode, stockName:String;
begin
  postmessage(tdx,WM_KEYDOWN,VK_F7,0);

  Sleep(3000);
  
  tradedate := DateToStr(Now);
  h1:= GetVisibleDialogHWND(tdxWnd);

  h2 := FindWindowEx(h1,0, nil, '输 出');
  postmessage(h2, WM_LBUTTONDOWN, 0, 0);
  postmessage(h2, WM_LBUTTONUP, 0, 0);
  sleep(1000);
  h2 := FindWindowEx(0,0,'#32770', '输出');
  h1 := GetWindow(h2, GW_CHILD);
  postmessage(h1, WM_LBUTTONDOWN, 0, 0);
  postmessage(h1, WM_LBUTTONUP, 0, 0);

  h1 := GetWindow(h1, GW_HWNDNEXT);
  h1 := GetWindow(h1, GW_HWNDNEXT);
  fileName := GetText(h1);

  h1 := FindWindowEx(h2,0, nil, '确  定');
  postmessage(h1, WM_LBUTTONDOWN, 0, 0);
  postmessage(h1, WM_LBUTTONUP, 0, 0);
  Sleep(2000);


  sline := TStringList.Create;
  sl := TStringList.Create;
  sl.LoadFromFile(fileName);

  DeleteFile(fileName);

  fileName := ExtractFileName(fileName) + ' - 记事本';
  h1 := FindWindowEx(0, 0, 'Notepad', PChar(fileName));
  postmessage(h1, WM_CLOSE, 0, 0);


  Memo1.Lines.Clear;
  Memo1.Lines.Add('trade log start');
  Memo1.Lines.Add(sl.GetText);
  Memo1.Lines.Add('trade log end');

    for i:=3 to sl.Count - 1 do begin
      line := sl[i];
      if AnsiContainsText(line, '没有相应的查询信息') then break;

      stockCode := LeftStr(line, 6);
      Delete(line, 1, 16);
      stockName := AnsiLeftStr(line, 8);
      stockName := Trim(stockName);
      Delete(line, 1, 16);

      sline.Delimiter := ' ';
      sline.DelimitedText := line;
      if sline[3] = '.00' then continue;


      ADOQuery.SQL.Clear;
      ADOQuery.SQL.Add(Format('insert into tradelog(loginid, tradetime,stockcode, stockname, operation, tradeamount, tradeprice, trademoney, contractno, tradeno, tradedate) values(''gengke'',''%s'',''%s'',''%s'',''%s'',%s,%s,%s,%s,''%s'',''%s'')', [tradedate + ' ' + sline[7],stockCode,stockName,sline[0],sline[1],sline[2],sline[3],sline[5],sline[6],tradedate]));
      ADOQuery.ExecSQL;
    end;


  sl.Free;
  sline.Free;
end;

procedure TfrmMain.btnHistoryClick(Sender: TObject);
var
  sl,sline:TStringList;
  i:Integer;
  tradeDate, line, stockCode,stockName:string;
begin
  if dlgOpen.Execute then begin
    sline := TStringList.Create;
    sl := TStringList.Create;
    sl.LoadFromFile(dlgOpen.FileName);

    for i:=3 to sl.Count - 1 do begin
      line := sl[i];
      tradeDate := LeftStr(line, 8);
      Delete(line, 1, 16);
      stockCode := LeftStr(line, 6);
      Delete(line, 1, 16);
      stockName := AnsiLeftStr(line, 8);
      stockName := Trim(stockName);
      Delete(line, 1, 16);

      sline.Delimiter := ' ';
      sline.DelimitedText := line;
      ADOQuery.SQL.Clear;
      ADOQuery.SQL.Add(Format('insert into tradelog(loginid, stockcode, stockname, operation, tradeamount, tradeprice, trademoney, contractno, tradedate) values(''%s'',''%s'',''%s'',''%s'',%s,%s,%s,%s,''%s'')', ['gengke', stockCode,stockName,sline[0],sline[1],sline[2],sline[3],sline[4], tradeDate]));
      Memo1.Lines.Add(ADOQuery.SQL.GetText);
      ADOQuery.ExecSQL;
    end;
    sl.Free;
    sline.Free;
  end;
end;

procedure TfrmMain.btnStopClick(Sender: TObject);
begin
  tmrOperate.Enabled := false;
end;

procedure TfrmMain.btnAccountClick(Sender: TObject);
var
  sl,sline:TStringList;
  i:Integer;
  tradeDate, line, stockCode,stockName:string;
  famount,remain,available,stock,total,profit:String;
begin
  if dlgOpen.Execute then begin
    sline := TStringList.Create;
    sl := TStringList.Create;
    sl.LoadFromFile(dlgOpen.FileName);

  sline.Delimiter := ' ';
  sline.DelimitedText := sl[0];
  remain := sLine[1];
  Delete(remain, 1 , Pos(':', remain));
  available := sLine[2];
  Delete(available, 1 , Pos(':', available));
  stock := sLine[3];
  Delete(stock, 1 , Pos(':', stock));
  total := sLine[4];
  Delete(total, 1 , Pos(':', total));
  profit := sLine[5];
  Delete(profit, 1 , Pos(':', profit));

  remain :=FloatToStr(StrToFloat(total) - StrToFloat(stock));

  tradedate := DateToStr(dt.Date);
  ADOQuery.SQL.Add(Format('insert into account(loginid, remain, stock, total, profit, tradedate) values(''gengke'',%s,%s,%s,%s,''%s'')', [remain,stock,total,profit,tradedate]));
  adoquery.ExecSQL;


    for i:=4 to sl.Count - 1 do begin
    line := sl[i];
    stockCode := LeftStr(line, 6);
    Delete(line, 1, 16);
    stockName := AnsiLeftStr(line, 8);
    stockName := Trim(stockName);
    Delete(line, 1, 16);
    sline.Delimiter := ' ';
    sline.DelimitedText := line;

    if (sline[9] = 'A310556598') or (sline[9] = '0143924068') then famount := '0'
    else famount := sline[9];

    ADOQuery.SQL.Clear;
    ADOQuery.SQL.Add(Format('insert into stockaccount(loginid, stockcode, stockname, stockamount, sellable, buyedamount, costprice, buyprice, currentprice, currentmoney, profit, plpercent, frozenamount, tradedate) values(''gengke'', ''%s'',''%s'',%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,''%s'')', [stockCode,stockName,sline[0],sline[1],sline[2],sline[3],sline[4],sline[5],sline[6],sline[7],sline[8],famount,tradedate]));
    ADOQuery.ExecSQL;
  end;

    sl.Free;
    sline.Free;
  end;
end;

function TfrmMain.GetWindowHWND(Caption: String): HWND;
var
  h:HWND;
  title:String;

begin
  h := FindWindowEx(0,0,'#32770', nil);

      while (h <> 0) do begin
        title := GetText(h);

        if AnsiStartsText(Caption, title) then begin
        result := h;
        exit;
      end;

        h := GetWindow(h,GW_HWNDNEXT);
     end;

     result := 0;
end;

function TfrmMain.GetVisibleDialogHWND(parent: HWND): HWND;
var
  h:HWND;
begin
  h := FindWindowEx(parent,0,'#32770', nil);

      while (h <> 0) do begin
        if IsWindowVisible(h) then begin
          result := h;
          exit;
        end;

        h := GetWindow(h,GW_HWNDNEXT);
     end;

     result := 0;
end;

function TfrmMain.GetGDDialog(Caption: String): String;
var
  h,h1,child,yesBtn, closeBtn:HWND;
  pCaption: PChar;
  CaptionLength :Integer;
begin
   h := FindWindowEx(0,0,'TfrmDialogs', PChar(Caption));

   h := GetWindow(h,GW_CHILD); //TPanel
   h := GetWindow(h,GW_HWNDNEXT); //TPanel
   h := GetWindow(h,GW_CHILD); //TGridPanel

   yesBtn := FindWindowEx(h,0,'Tbutton', '是(&Y)');
   
   if yesBtn = 0 then
     yesBtn := FindWindowEx(h,0,'Tbutton', '确定(&O)');

   postmessage(yesBtn,WM_LBUTTONDOWN,0,0); //按下鼠标
   postmessage(yesBtn,WM_LBUTTONUP,0,0);  //释放鼠标


end;

procedure TfrmMain.CancelGDDelegation;
var
  h1,panel,okBtn:HWND;
begin
  postmessage(mnuWnd, WM_LBUTTONDOWN, 0, MakeLParam(160,10));
  postmessage(mnuWnd, WM_LBUTTONUP, 0, MakeLParam(160,10));

  Sleep(3000);
  GetGDDialog('提示');
  Sleep(2000);

  h1:= FindWindowEx(tdxWnd, 0, 'TFrmBatchWithDraw', nil);
  panel := FindWindowEx(h1,0,'TPanel', nil);
  okBtn := FindWindowEx(panel,0,'TButton', '撤全部(F7)'); //撤单

  postmessage(okBtn, WM_LBUTTONDOWN, 0, MakeLParam(160,10));
  postmessage(okBtn, WM_LBUTTONUP, 0, MakeLParam(160,10));
  sleep(3000);
  GetGDDialog('警告');
  sleep(2000);  
  GetGDDialog('选择');
  sleep(2000);
  GetGDDialog('提示');
  sleep(2000);  
  GetGDDialog('提示');
end;

procedure TfrmMain.CalGDTrade;
var
  h1,h2,panel1,panel2,btn:HWND;
  id,tradedate,line,fileName,stockCode,stockname,tradetime,price,amount,oper,money,tradeno:String;
  sl,sline:TStringList;
  i:Integer;
begin
  tradedate := DateToStr(now);

      postmessage(mnuWnd, WM_LBUTTONDOWN, 0, MakeLParam(20,10));
      postmessage(mnuWnd, WM_LBUTTONUP, 0, MakeLParam(20,10));

      Sleep(3000);
      h1:= FindWindowEx(tdxWnd, 0, 'TFrmBuyStock1', nil);
      h1 := GetWindow(h1,GW_CHILD); //TPanel
      h1 := GetWindow(h1,GW_CHILD); //TPageControl

      postmessage(h1, WM_LBUTTONDOWN, 0, MakeLParam(150,5));
      postmessage(h1, WM_LBUTTONUP, 0, MakeLParam(150,5));
      h1:= FindWindowEx(h1, 0, 'TTabSheet', '成交[F8]');
      Sleep(3000);


      h1:= FindWindowEx(h1, 0, 'TPanel', nil);
      h1:= FindWindowEx(h1, 0, 'TBitBtn', '输出');
      postmessage(h1,WM_LBUTTONDOWN,0,0); //按下鼠标
      postmessage(h1,WM_LBUTTONUP,0,0);  //释放鼠标
      Sleep(3000);
      
       h2 := FindWindowEx(0,0,'TfrmSetting', '输出选择');
       if h2 = 0 then exit;
       
  panel1 := GetWindow(h2, GW_CHILD);
  panel2 := GetWindow(panel1, GW_HWNDNEXT);
    
  panel1 := getWindow(panel1, GW_CHILD); // TfrmPrintSetting
  h1 := FindWindowEx(panel1, 0, 'TRadioButton', '输出到文件');
  postmessage(h1, WM_LBUTTONDOWN, 0, 0);
  postmessage(h1, WM_LBUTTONUP, 0, 0);
  h1 := FindWindowEx(panel1, 0, 'TRadioGroup', '');
  h1 := FindWindowEx(h1, 0, 'TGroupButton', '文本');
  postmessage(h1, WM_LBUTTONDOWN, 0, 0);
  postmessage(h1, WM_LBUTTONUP, 0, 0);

  Sleep(1000);
  h1 := FindWindowEx(panel1, 0, 'TEdit', nil);
  fileName := GetText(h1);


  btn := FindWindowEx(panel2, 0, 'TButton', '确定(&O)');
  postmessage(btn, WM_LBUTTONDOWN, 0, 0);
  postmessage(btn, WM_LBUTTONUP, 0, 0);



  Sleep(2000);



  sline := TStringList.Create;
  sl := TStringList.Create;
  sl.LoadFromFile(fileName);

  DeleteFile(fileName);

  fileName := ExtractFileName(fileName) + ' - 记事本';
  h1 := FindWindowEx(0, 0, 'Notepad', PChar(fileName));
  postmessage(h1, WM_CLOSE, 0, 0);

  sline.Delimiter := ' ';
  sline.DelimitedText := sl[1];



  for i:=1 to sl.Count - 1 do begin
    line := sl[i];
    if AnsiStartsStr('合计', line) then break;
    sline.Delimiter := ' ';
    sline.DelimitedText := line;

    id := sline[0];
    tradetime := sline[1];
    stockCode := sline[3];
    stockName := sline[4];
    price := sline[5];
    amount := sline[6];
    oper := sline[7];
    tradeno := sline[8];
    money := sline[9];

    ds.Active := false;
    ds.CommandText := 'select * from tradelog where loginid=''sunsulian'' and tradeno=' + tradeno;
    ds.Active := true;

    if ds.Eof then begin

      ADOQuery.SQL.Clear;
      ADOQuery.SQL.Add(Format('insert into tradelog(loginid, time, stockcode, stockname, tradeprice, tradeamount, operation, tradeno, trademoney, tradedate, id) values(''sunsulian'', ''%s'',''%s'',''%s'',%s,%s,''%s'',%s,%s,''%s'',%s)', [tradetime, stockCode,stockName,price,amount,oper,tradeno,money,tradedate, id]));
      ADOQuery.ExecSQL;
    end;
  end;
  sl.Free;
  sline.Free;


  ds.Active := false;
  ds.CommandText := 'select stockcode, operation, sum(trademoney) as money from tradelog where loginid=''sunsulian'' and tradedate=' + QuotedStr(tradedate) + ' group by tradeno';
  ds.Active := true;

  while not ds.Eof do begin
    stockCode := ds.fieldbyname('stockcode').AsString;
    oper := ds.fieldbyname('operation').AsString;
    money := ds.fieldbyname('money').AsString;
    if oper = '卖' then oper := 'BUY'
    else oper := 'SELL';

    ds1.Active := false;
    ds1.CommandText := 'select pkid from stockaccount where stockcode=' + QuotedStr(stockCode) + ' and loginid=''sunsulian'' order by tradedate desc limit 0,1';
    ds1.Active := true;

    if not ds1.Eof then begin
      ADOQuery.SQL.Clear;
      ADOQuery.SQL.Add('update stockaccount set oper=' + QuotedStr(oper) + ', amount=' + money +', operdate=' + QuotedStr(tradedate) + ' where pkid=' + ds1.fieldbyname('pkid').AsString);
      ADOQuery.ExecSQL;
    end;
    
    ds.Next;

  end;
end;

end.
