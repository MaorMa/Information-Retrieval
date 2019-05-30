package Model.Parsers.ParsingProcess;
import Model.DataObjects.ParseableObjects.IParseableObject;

/**
 * all Parsers must implements the parsing method
 */

public interface IParsingProcess {
    void parsing(IParseableObject IParseableObject);
}
