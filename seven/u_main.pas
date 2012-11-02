unit u_main;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, CoolTrayIcon, Menus, MSNPopUp, ComCtrls, DB, ADODB, ExtCtrls,
  HTTPGet, StrUtils, StdCtrls, ImgList, AdvListV;

type
  TForm1 = class(TForm)
    trayIcon: TCoolTrayIcon;
    pm: TPopupMenu;
    N1: TMenuItem;
    msnp: TMSNPopUp;
    N2: TMenuItem;
    con: TADOConnection;
    ds: TADODataSet;
    stat: TStatusBar;
    httpget: THTTPGet;
    tmr: TTimer;
    query: TADOQuery;
    btn: TButton;
    lv: TAdvListView;
    procedure N1Click(Sender: TObject);
    procedure msnpClick(Sender: TObject);
    procedure dtChange(Sender: TObject);
    procedure tmrTimer(Sender: TObject);
    procedure httpgetDoneString(Sender: TObject; Result: String);
    procedure FormDestroy(Sender: TObject);
    procedure btnClick(Sender: TObject);
    procedure httpgetError(Sender: TObject);
    procedure FormCreate(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
    procedure GetPrice(nIndex:Integer);
  end;

var
  Form1: TForm1;

implementation

{$R *.dfm}

procedure TForm1.N1Click(Sender: TObject);
begin
  trayIcon.ShowMainForm;
end;

procedure TForm1.msnpClick(Sender: TObject);
begin
trayIcon.ShowMainForm;
msnp.ClosePopUps;
end;

procedure TForm1.dtChange(Sender: TObject);
var
  item:TListItem;
  ct:integer;
  p:Pointer;
  price,sevenprice:Double;

begin
  lv.Items.Clear;
  ds.CommandText := 'select * from stocklimit where statusid=''402880823a8b8ac8013a8b8bf5140001''';
  ds.Open;
  ct := ds.RecordCount;
  while not ds.Eof do begin
    price := ds.fieldbyname('closeprice').AsFloat;
    sevenprice := ds.fieldbyname('sevenprice').AsFloat;

//    if (sevenprice >= price * 0.9) then begin

       item := lv.Items.Add;
       GetMem(P, SizeOf(Integer));
       item.Data := P;
       Integer(item.Data^) := ds.FieldByName('pkid').asInteger;
       item.Caption := ds.FieldByName('stockcode').AsString;
       item.SubItems.Add(ds.fieldbyname('stockname').AsString);
       item.SubItems.Add(ds.fieldbyname('minprice').AsString + '元');
       item.SubItems.Add(ds.fieldbyname('maxprice').AsString + '元');
       item.SubItems.Add(ds.fieldbyname('initpercent').AsString + '%');
       item.SubItems.Add(ds.fieldbyname('sevenprice').AsString + '元');
       item.SubItems.Add('0.00元');
       item.SubItems.Add('0.00%');
       item.SubItems.Add('未知');
//     end;
     ds.Next;
  end;
  ds.Close;

  stat.Panels[0].Text := '共' + IntToStr(ct) + '只股票符合';
  lv.SortType:= stData;
  lv.SortColumn := 7;
end;

procedure TForm1.tmrTimer(Sender: TObject);
var
  nIndex : Integer;
begin
  tmr.Enabled := False;
  GetPrice(tmr.Tag);
  tmr.Tag := tmr.Tag + 1;
end;

procedure TForm1.httpgetDoneString(Sender: TObject; Result: String);
var
    ChangedItem:   TListItem;
    nIndex: integer;
    sl:TStringList;
    sql, stockcode, pkid, price:String;
    yesterday, percent, lowprice, sevenprice:Double;
begin
  Result := StringReplace(Result, ' ', '', [rfReplaceAll]);

  sl := TStringList.Create;
  sl.Delimiter := ',';
  sl.DelimitedText := Result;
  stockcode := sl[0];
  Delete(stockcode, 1, 12);
  stockcode := LeftStr(stockcode, 6);



  if sl[3] = '0.00' then begin
    tmr.Enabled := true;
    exit;
  end;


  yesterday := StrToFloat(sl[2]);

      for   nIndex   :=   lv.Items.Count   -   1   downto   0   do
      begin
            if  stockcode   =   lv.Items[nIndex].Caption  then
            begin

                ChangedItem   :=   lv.Items.Item[nIndex];
                ChangedItem.SubItems.Strings[5]   :=   sl[5] + '元';
                ChangedItem.SubItems.Strings[6]   :=   sl[3] + '元';

                price := ChangedItem.SubItems.Strings[4];
                price := LeftStr(price, Length(price)-2);

                sevenprice := StrToFloat(price);

                if (yesterday * 0.9) > sevenprice then begin
                  lv.Items.Delete(nIndex);
                  stat.Panels[0].Text := '共' + IntToStr(lv.Items.Count) + '只股票符合';                  
                  break;
                end;


                lowprice := StrToFloat(sl[5]) + 0.03;

                if (sevenprice >= lowprice) and (ChangedItem.SubItems.Strings[7] <> '符合') then begin
                  ChangedItem.SubItems.Strings[7] := '符合';
                  msnp.Title := '股票提示';
                  msnp.Text := stockcode + '（' + ChangedItem.SubItems.Strings[0] + '）' + '符合七寸点,当前价格:' + ChangedItem.SubItems.Strings[5];
                  msnp.ShowPopUp;


                  trayIcon.ShowMainForm;
                  lv.SetFocus;
                  ChangedItem.Selected := true;
                  pkid := IntToStr(Integer(ChangedItem.Data^));
                  sql := 'update stocklimit set matchdate=' + QuotedStr(FormatDateTime( 'yyyy-mm-dd', now)) + ' , statusid=''402880823a8b8ac8013a8b8c262b0002'' where pkid=' + pkid;
                  query.sql.Clear;
                  query.SQL.Add(sql);
                  query.ExecSQL;
                end;

                break;
            end;
      end;
  tmr.Enabled := true;
end;

procedure TForm1.GetPrice(nIndex: Integer);
var
  stockcode:String;
begin
  if nIndex >= lv.Items.Count then begin
    tmr.Tag := -1;
    tmr.Enabled := true;
    lv.Sort;
    exit;
  end;
  
  stockcode := lv.Items[nIndex].Caption;

  if lv.Items[nIndex].SubItems[6] = '符合' then begin
    tmr.Enabled := true;
    exit;
  end;

  stat.Panels[1].Text := '正在查询' + lv.Items[nIndex].SubItems[0];
  if LeftStr(stockcode,1) = '6' then
  stockcode := 'sh' + stockcode
  else stockcode := 'sz' + stockcode;

  httpget.URL := 'http://hq.sinajs.cn/list=' + stockcode;
  httpget.GetString;
end;

procedure TForm1.FormDestroy(Sender: TObject);
var
  I: Integer;
begin
  for I := 0 to lv.Items.Count - 1 do
    with lv.Items[I] do FreeMem(Data, SizeOf(Integer));
end;



procedure TForm1.btnClick(Sender: TObject);
begin
  if tmr.Enabled then begin
    tmr.Enabled := False;
    btn.Caption := '开始扫描';
  end
  else begin
    tmr.Enabled := true;
    btn.Caption := '停止扫描';
  end;
end;

procedure TForm1.httpgetError(Sender: TObject);
begin
  tmr.Enabled := true;
end;

procedure TForm1.FormCreate(Sender: TObject);
begin
dtChange(Sender);
end;

end.
