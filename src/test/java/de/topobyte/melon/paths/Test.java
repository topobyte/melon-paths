package de.topobyte.melon.paths;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Test
{

	public static void main(String[] args) throws IOException
	{
		List<Path> files = PathUtil
				.findRecursive(Paths.get("/home/z/github/topobyte"), "*.md");
		for (Path file : files) {
			System.out.println(file);
		}
	}

}
