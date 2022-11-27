// Copyright 2022 Sebastian Kuerten
//
// This file is part of melon-paths.
//
// melon-paths is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// melon-paths is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with melon-paths. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.melon.paths;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TestFindMarkdownAndGradle
{

	public static void main(String[] args) throws IOException
	{
		List<Path> files = PathUtil.findRecursive(
				Paths.get("/home/z/github/topobyte"),
				Arrays.asList("*.md", "*.gradle"));
		for (Path file : files) {
			System.out.println(file);
		}
	}

}
