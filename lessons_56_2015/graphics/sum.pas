Program Space; {�������� ������� ������ �., ���.���. ����}
  Uses Graph, Crt;
  Const
    RadOrb = 250 {������ ������ �����}; RadSun = 70 {������ ������};
    RadGal = 100 {������ ��������� }; RadZem = 18 {������ ����� };
    Naklon = 0.2 {����������� ������� ��������� ������ �����};
    PressZem = 0.65 {����������� ������������ ������� �����};
    Compress = 0.8 {����������� ������ ��� �������� �� };
                   {���������� ������ VGA � ����� CGA }
  Var
    ZemX, ZemY, UgMer, PixelY, DUgZem , UpDown,
    XRad, Grad, UgZem, PixelX, StAngle, Ua, Ub,
    ParallelY , Color, ZemPix, EndAngle,
    VisualPage, GrMode, GrError, GrDriver, i : Integer;
    Ugol, CompressZem, Expansion,
    DUgol, Projection, PolUgol : Real;
BEGIN
  {��������� ������������ ������ � �������� ��������� ������}
  GrDriver := EGA; GrMode := EGAHi;
  InitGraph(GrDriver, GrMode, 'C:\TP\BGI');
  GrError := GraphResult; If GrError<>GrOk then Halt;
  SetBkColor(Black);
  SetFillStyle(1, Yellow); {��������� ����� ���������� � ����� C����a}
  Ugol := 0; DUgol := 2*Pi/180; {����������� ������� �������� �����}
  UgZem := 0; DUgZem := 14; {������ ������� �������� �����}
  {------------------------------------------------------------------}
  VisualPage := 1;
  Repeat {���� ����������� �������� ����� �������}
    SetVisualPage(1- (VisualPage mod 2));
         {��������� ������ ������� �������������}
    VisualPage := VisualPage+1; {�������� ������������}
    SetActivePage(1 - (VisualPage mod 2));
         {��������� ������ ��������� (��������) �������������,}
         {������������ ��� ���������� ���������� ����������� }
    ClearDevice; {������� ������������ ������}
     {--------------------------------------------------------------}
    {��������� "������������" ���������}
    RandSeed:=1; {�������� �������� ������� ��������� �����}
    Expansion:=VisualPage/100; {c������� ���������� ���������}
    For i:= 1 to VisualPage do
      begin XRad := Trunc(Expansion*RadGal*Random);
             {������� ���������� �� ������ �� ������ ���������}
        PolUgol:= 2*Pi*Random-VisualPage/30;
             {������� ����������� ���� ��������� ������ ���������}
        PixelX := 370+Trunc(XRad*cos(PolUgol+1.8)); {����������}
        PixelY := 250+Trunc(XRad*0.5*sin(PolUgol)); { ������ }
        PutPixel(PixelX, PixelY, White) {��������� ������}
      end;
     {--------------------------------------------------------------}
    {��������� ��������� �����}
    Randomize; {������������� ������� ��������� �����}
    For i:=1 to 70 do
      PutPixel(Random(640),Random (350),White); {������������ ������}
     {--------------------------------------------------------------}
    For i := 1 to 100 do {��������� ������}
      PutPixel(320+Round(RadOrb * cos((i+VisualPage/5)*Pi/50+0.3)),
      160+Round(RadOrb*Naklon*sin((i+VisualPage/5)*Pi/50-Pi/2)),15);
     {--------------------------------------------------------------}
    PieSlice(310, 160, 0, 360, RadSun); {��������� ������}
     {--------------------------------------------------------------}
    {��������� ����� (�� ���������� � ����������)}
    Ugol := Ugol+DUgol ; {���� �������� ����� ������������ ������}
    Grad := Round(180*Ugol/Pi) mod 360; {� ���.(Ugol) � � ����.(Grad)}
    ZemX := 320+Round(RadOrb*cos((Ugol+Pi/2+0.3))); { ���������� }
    ZemY:=160+Round(RadOrb*Naklon*sin(Ugol)); {������ �����}
    CompressZem := 2.5-cos(Ugol+0.3);
           {����������� ����� ����������� ����� �� �����������}
    ZemPix := Round(RadZem*CompressZem); {������� ������ �����}
    UgZem := UgZem+DUgZem; {���� �������� ����� ������������ ����� ���}
    For i := 0 to 11 do { ��������� ���������� }
      begin
        UgMer := (UgZem+i*30) mod 360;
        If (90<UgMer) and (UgMer<270) {��������� ���������� � ���������}
          then begin StAngle := 90; EndAngle := 270 end { ����� ���� }
          else begin StAngle := 270; EndAngle := 90 end; {������� ���������}
        Ua := (Grad+220) mod 360; Ub := (Grad+400) mod 360;
           {��������� ������ ��������� ���������� � ����������
            ������ ���������}
        Color := LightBlue;
        If Ua<=Ub then if (Ua<UgMer) and (UgMer<Ub) then Color := White;
        If Ua >Ub then if (Ua<UgMer) or (UgMer<Ub) then Color := White;
        SetColor(Color);
        XRad := round((ZemPix*cos(UgMer*Pi/180))); 
        Ellipse(ZemX,ZemY,StAngle,EndAngle,abs(XRad),round(PressZem*ZemPix));
      end;
    For i := 2 to 7 do {��������� ����������}
      begin
        XRad := abs(Round(ZemPix*sin(i*Pi/9)));
           {������� ������� ������� ���������}
        UpDown := Round(ZemPix*PressZem*cos(i*Pi/9));
           {������ ��������� ��� ���������� ��������}
        ParallelY := ZemY+UpDown; {���������� Y ������ ������� ���������}
        SetColor(LightBlue);
        Ellipse(ZemX, ParallelY, 0, 360, XRad, Round(Naklon*XRad));
           {���������� ����� ���������}
        SetColor(White);
        Ellipse(ZemX,ParallelY,Grad+220,Grad+400,XRad,Round(Naklon*XRad));
           {���������� ����� ���������}
      end;
     {------------------------------------------------------------------}
    {��������� ��������� C�����, ���� ��� ����� � �����������, ��� �����}
    If CompressZem<2 then PieSlice(310, 160, 0, 360, RadSun);
     {------------------------------------------------------------------}
    RandSeed := VisualPage mod 12;
    For i := 1 to 250 do {��������� �������������}
      begin
        Projection := (1-sqr(Random))*Pi/2;
        XRad := RadSun+Round((20)*sin(Projection))-15;
        PolUgol := 2 * Pi * Random+VisualPage/20;
        {PolUgol, XRad - �������� ���������� ������������}
        PixelX := 310 + Round( XRad * cos(PolUgol));
        PixelY := 160 + Round( Compress * XRad * sin(PolUgol));
        PutPixel(PixelX, PixelY, LightRed)
      end;
  until KeyPressed 
END.