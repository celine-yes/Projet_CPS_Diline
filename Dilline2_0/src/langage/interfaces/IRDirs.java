package langage.interfaces;

import java.util.List;

import langage.ast.Dir;

public interface IRDirs extends IDirs{
	public Dir getDir();
	public List<Dir> getDirs();
}
