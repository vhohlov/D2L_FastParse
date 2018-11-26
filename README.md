# D2L_FastParse
# Organizare si initializare parser

Fiecare mod de formatare a sintaxei DokuWiki(Basic Text Formatting, Lista, etc) e definit in cadrul unui clase separate ce extinde radacina Mode.
 
In cadrul radacinei Mode sunt definite proprietatile unui mod de formatare(Group, order, allowedGroups) si cateva parsere  comune pentru toate modurile.
Parserul "content" reprezinta continutul care se poate gasi in cadrul modului de formatare curent. Prin metoda "addMode", se adauga moduri ce pot formata continutul
modului curent.

Modurile sunt  grupate in 7 grupuri conform actiunilor pe care le indeplinesc si a proprietatilor de combinare a sintaxei cu celelalte moduri (Grupurile sunt similare cu cele folosite in constructia parserului DokuWiki) :

Ungrouped - grup special in care se afla modul de pornire (Base Mode)

BaseOnly - moduri de formatare ce se pot regasi doar in cadrul modului Base

Formatted - moduri ce se ocupa de stilizarea textului afisat. Aceste moduri pot contine si alte moduri.

Container - moduri ce pot contine alte moduri, dar nu se ocupa de stilizarea textului afisat(lista, tabel)

Substitution - moduri in care intreg textul reprezinta un token ce este substituit de o alta structura

Protected - moduri ce incep si se termina cu un tag si nu pot contine alte moduri

Disabled - moduri in care nu se aplica sintaxa dokuwiki


Ordinea in care modurile se regasesc in cadrul parserului conteaza, Fiecare mod are setata o anumita pondere de ordine.  
Fiecare mod accepta anumite grupuri de moduri ce pot sa parseze continutul modului curent.

Plugin-urile respecta aceeasi structura logica ca si modurile predefinite.
Plugin-urile disponibile in cadrul aplicatie vor fi activate sau dezactivate de catre utilizator.


La rularea programului, in functie de ponderile si grupurile care sunt acceptate de fiecare mod si plugin-in activat,  se creaza parserul.

# Input - Output
In implementarea actuala, inputul este preluat de pe un repository Dokuwiki de la distanta. Inputul reprezinta o singura pagina(fisier sursa) DokuWiki.
Clasa "RemoteIO" identifica fisierele din DokuWiki chiar daca sunt aplicate reguli de REWRITE de catre server sau de catre DokuWiki.
Singura conditie de functionare e ca wiki-ul sa permita exportarea surselor.

Output -ul reprezinta urmatoarea structura de fisiere:
- {srcName}_LaTex - directorul cu toate sursele folosite de aplicatie
  - DokuWiki -> directorul ce contine sursa DokuWiki 
  - Media -> directorul cu imaginile descarcate din sursa DokuWiki
  - srcName.tex -> fisierul cu sursa LaTex generata



Pot fi create alte moduri de prelucrare Input-Output, atat timp cat clasa creata respecta trait-ul "IO"



Progres actual:
In momentul de fata urmatoarele moduri functioneaza :

- toate tipurile de stilizare text(reproduce 100% acelasi output ca in DokuWiki, inclusiv si situatiile de formatare necorespunzatoare)

- listele (reproduce 100% acelasi output ca in DokuWiki, inclusiv si situatiile de formatare necorespunzatoare)
- Header(reproduce 100% acelasi output ca in DokuWiki)
- Toate tipurile de cod
  -e folosit package-ul minted pentru formatarea codului. Acest package necesita instalarea aplicatiei "pigments"
- Quoting
- Footnote
- Link-uri
  - identifica toate tipurile de linkuri(www, wiki curent, wiki shortcuts)
  - Pentru linkuri cu text descriptiv,  afiseaza in pagina textul si creaza un footnote cu linkul specificat 
- Media
- Color Plugin
- MathJax plugin
- Math plugin
  -Pentru teoreme, definitii, si proof va fi folosit package-ul tcolorbox pentru afisarea placuta a acestor elemente

# Rulare aplicatie:
- Build tool: sbt
- comanda: run {DokuWiki URL}


In fisierul "exemple" gasiti output-ul generat la aceasta etapa de implementare pentru urmatoarele pagini:
- http://ocw.cs.pub.ro/ppcarte/doku.php?id=lfa:cfg
- http://ocw.cs.pub.ro/ppcarte/doku.php?id=pp:intro
- http://ocw.cs.pub.ro/ppcarte/doku.php?id=lfa:nfa
