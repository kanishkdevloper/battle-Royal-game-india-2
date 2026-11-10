#!/usr/bin/env python3
"""Builds the credits of every Sketchfab model automatically.

A .glb / .gltf downloaded from Sketchfab carries its author, licence, title and source URL inside the file
(asset.extras). Put the ORIGINAL downloads of your models (plane, vehicles, character, guns, clothes ...) into
reference/sketchfab/ (any sub-folders), then run from the project root:

    python3 tools/extract_credits.py

It rewrites CREDITS.md and app/src/main/assets/credits.txt (shown in-game: Settings > ACCOUNT > CREDITS & LICENSES)
and prints a verdict per model:
    OK     CC0 / CC-BY (just credit - done by this script)
    CHECK  CC-BY-SA or unknown licence (read the licence; share-alike can reach your app)
    STOP   NonCommercial (NC), NoDerivatives (ND) or "Editorial" / "Standard" licences: NOT allowed in an app with ads
           -> replace the model or ask the author in writing.
Models without a .glb: add one line each to tools/extra_credits.txt:  Title | Author | Licence | URL
"""
import json, os, struct, sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SCAN = [os.path.join(ROOT, 'reference')]
AI_LINES = [
    "Lobby background and menu / HUD icons: created by the developer with ChatGPT (OpenAI, image generation) and used under OpenAI's Terms of Use, which give the user the rights to the output, including commercial use.",
    "Sounds are generated in code. Game code by the developer.",
    "Aircraft (passenger jet): designed with AI assistance and owned by the developer (tools/make_plane.py).",
    "Reload animation: original motion written for this game (ReloadAnim.kt), no third-party data.",
    "CHANGES NOTICE (CC BY 4.0): the works marked CC-BY-4.0 above were modified for this game: converted to the game's own file format, textures reduced in size, scaled / posed / re-coloured, and (M4 animation pack) only the reload motion was re-sampled and re-targeted onto the game's own guns and hands, and (Human character with jumping animation) only the jump motion was re-sampled and re-targeted onto the game's own character. The authors do not endorse this game. Licence text: https://creativecommons.org/licenses/by/4.0/",
    "Guns (13 weapon models): created procedurally by the developer (tools/make_guns.py), original work.",
]

def read_extras(path):
    with open(path, 'rb') as f:
        data = f.read()
    if path.lower().endswith('.glb'):
        if data[:4] != b'glTF':
            return None
        ln, typ = struct.unpack('<II', data[12:20])
        js = json.loads(data[20:20 + ln].decode('utf-8'))
    else:
        js = json.loads(data.decode('utf-8'))
    ex = (js.get('asset') or {}).get('extras') or {}
    return {k: str(ex.get(k, '')).strip() for k in ('title', 'author', 'license', 'source')}

def verdict(lic):
    u = lic.upper().replace(' ', '')
    if not u: return 'CHECK'
    if 'NC' in u.replace('CC-BY','').replace('CC0','') or '-ND' in u or 'EDITORIAL' in u or 'STANDARD' in u: return 'STOP'
    if 'CC0' in u or 'PUBLICDOMAIN' in u: return 'OK'
    if 'ROYALTYFREE' in u: return 'OK'   # CGTrader Royalty Free (no AI): OK in a game if users cannot extract the file (see proof_private/CHARACTER_LICENSE.md)
    if '-SA' in u: return 'CHECK'
    if 'CC-BY' in u: return 'OK'
    return 'CHECK'

def main():
    items, seen = [], set()
    for base in SCAN:
        for r, _, fs in os.walk(base):
            for fn in sorted(fs):
                if fn.lower().endswith(('.glb', '.gltf')):
                    p = os.path.join(r, fn)
                    try: e = read_extras(p)
                    except Exception as ex: print('cannot read', p, ex); continue
                    if e is None: continue
                    e['file'] = os.path.relpath(p, ROOT)
                    if not e['title']: e['title'] = os.path.splitext(fn)[0]
                    items.append(e); seen.add(e['source'] or e['file'])
    xp = os.path.join(ROOT, 'tools', 'extra_credits.txt')
    if os.path.exists(xp):
        for ln in open(xp, encoding='utf-8'):
            ln = ln.strip()
            if not ln or ln.startswith('#'): continue
            a = [x.strip() for x in ln.split('|')] + ['', '', '', '']
            items.append({'title': a[0], 'author': a[1], 'license': a[2], 'source': a[3], 'file': 'extra_credits.txt'})
    worst = 'OK'
    rows = []
    for e in items:
        v = verdict(e['license']); rows.append((v, e))
        if v == 'STOP': worst = 'STOP'
        elif v == 'CHECK' and worst != 'STOP': worst = 'CHECK'
        print('%-5s %-32s %-28s %s' % (v, e['title'][:32], e['license'][:28], e['file']))
    md = ['# Credits', '']
    txt = ['CREDITS & LICENSES', '']
    for v, e in rows:
        md.append('- **%s** by %s, %s\n  Source: %s' % (e['title'], e['author'] or '?', e['license'] or '?', e['source'] or '?'))
        txt.append('%s - by %s - %s\n%s\n' % (e['title'], e['author'] or '?', e['license'] or '?', e['source'] or ''))
    md += [''] + ['- ' + l for l in AI_LINES]
    txt += AI_LINES
    open(os.path.join(ROOT, 'CREDITS.md'), 'w', encoding='utf-8').write('\n'.join(md) + '\n')
    ap = os.path.join(ROOT, 'app', 'src', 'main', 'assets')
    open(os.path.join(ap, 'credits.txt'), 'w', encoding='utf-8').write('\n'.join(txt) + '\n')
    # keep manifest.txt size of credits.txt right
    mp = os.path.join(ap, 'manifest.txt')
    if os.path.exists(mp):
        sz = os.path.getsize(os.path.join(ap, 'credits.txt')); out = []; found = False
        for l in open(mp, encoding='utf-8').read().splitlines():
            if l.startswith('credits.txt|'): l = 'credits.txt|%d' % sz; found = True
            out.append(l)
        if not found: out.append('credits.txt|%d' % sz)
        open(mp, 'w', encoding='utf-8').write('\n'.join(out) + '\n')
    print('\n%d models listed. Overall: %s' % (len(rows), worst))
    if worst == 'STOP': print('At least one model has a licence that does not allow your app (NC / ND / Editorial). Replace it.')
    sys.exit(0 if worst != 'STOP' else 2)

if __name__ == '__main__':
    main()
